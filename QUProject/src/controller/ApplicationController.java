package controller;

import model.TimeQuery;
import service.ApplicationService;

import java.util.LinkedList;
import java.util.List;

public class ApplicationController {

    private ApplicationService appService = new ApplicationService();

    public void run() {

        List<TimeQuery> timeQueryList = appService.loadDataFromFile("input.txt");
        appService.waitingTime(timeQueryList);
    }
}
