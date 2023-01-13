package scout.tracker;

import scout.Scout;
import scout.model.UserModel;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

/**
 * Singleton instance of a checker that periodically checks tracker objects if an
 * item's price has changed.
 */
public class TrackerChecker {
    private static final String filename = "trackers.ser";
    private static TrackerChecker INSTANCE;
    private HashSet<Tracker> trackers;

    private TrackerChecker() {
        this.trackers = new HashSet<>();
        loadTrackers();
        run();
    }

    private void run() {
        Scout.service.scheduleAtFixedRate(this::checkTrackers, 0, 8, TimeUnit.MINUTES);
    }

    private void checkTrackers() {
        for(Tracker t : trackers) {
            Scout.service.submit(() -> {
                System.out.println("checking: " + t.getItemName());
                System.out.println("current price: " + t.getCurrentPrice());
                if(t.priceChanged()) {
                    System.out.println("price changed:" + t.getNewPrice() + " (diff: " + t.getPriceChange());
                    t.notifyAllUsers();
                    t.update();
                }
            });
        }
    }

    public static TrackerChecker getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new TrackerChecker();
        }
        return INSTANCE;
    }

    public boolean addTracker(Tracker tracker) {
        return trackers.add(tracker);
    }

    public Tracker getTracker(Tracker tracker) {
        for(Tracker t : trackers) {
            if(t.equals(tracker))
                return t;
        }

        return null;
    }

    public void loadTrackers() {
        if(!new File(filename).exists()) {
            return;
        }
        try (
            FileInputStream fin = new FileInputStream(filename);
            ObjectInputStream in = new ObjectInputStream(fin))
        {
            trackers = (HashSet<Tracker>)in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveTrackers() {
        if(trackers.isEmpty()) {
            return;
        }

        try(
            FileOutputStream fout = new FileOutputStream(filename);
            ObjectOutput oos = new ObjectOutputStream(fout))
        {
            oos.writeObject(trackers);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void clearEmptyTrackers() {
        trackers.removeIf(t -> t.getUsers().isEmpty());
    }

    public ArrayList<Tracker> getUserTrackers(UserModel userModel) {
        ArrayList<Tracker> userTrackers = new ArrayList<>();
        for(Tracker t : trackers) {
            if(t.getUsers().contains(userModel))
                userTrackers.add(t);
        }
        return userTrackers;
    }
}
