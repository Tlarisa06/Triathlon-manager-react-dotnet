package ro.mpp.triathlon.repository;


import ro.mpp.triathlon.model.Referee;

public interface IRefereeRepo extends IRepository<Integer, Referee> {
    // Metodă specifică pentru verificarea credențialelor la Login
    Referee findByUsernameAndPassword(String username, String password);
}