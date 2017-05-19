package service;

import model.TimeQuery;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ApplicationService {

    public List<TimeQuery> loadDataFromFile(String inputFile) {

        List<TimeQuery> timeQueryList = null;
        BufferedReader br = null;
        String line = null;

        try {
            br = new BufferedReader(new FileReader(inputFile));
            line = br.readLine();
            int loopCount = Integer.parseInt(line);
            timeQueryList = new LinkedList<TimeQuery>();

            for (int i = 0; i < loopCount + 1; i++ ) {

                String[] data = line.split(" ");

                if ("C".equals(data[0])) {
                    timeQueryList.add(createObjectC(data));
                } else if ("D".equals(data[0])) {
                    timeQueryList.add(createObjectD(data));
                }

                line = br.readLine();
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return timeQueryList;
    }

    public void waitingTime(List<TimeQuery> timeQueryList) {

        List<Double> times;
        int currentPosition = 0;
        int dPosition = 0;

        for (TimeQuery timeQuery : timeQueryList) {
            if ("D".equals(timeQuery.getObjectType())) {
                dPosition = currentPosition;
                times = new LinkedList<Double>();

                for (int j = 0; j < dPosition; j++) {
                    TimeQuery timeQueryCompared = timeQueryList.get(j);

                    if ("C".equals(timeQueryCompared.getObjectType())
                            && meetsCriteria(timeQuery, timeQueryCompared)) {
                        times.add(timeQueryCompared.getTime());
                    }
                }
                if (times.size() > 0) {
                    System.out.println(averageTime(times));
                } else {
                    System.out.println("-");
                }
            }
            currentPosition++;
        }
    }

    public boolean meetsCriteria(TimeQuery timeQuery, TimeQuery timeQueryCompared) {

        return compareServiceId(timeQuery, timeQueryCompared)
                && compareQueryTypeId(timeQuery, timeQueryCompared)
                && compareResponseType(timeQuery, timeQueryCompared)
                && compareDates(timeQuery, timeQueryCompared);
    }

    public int averageTime(List<Double> times) {

        double timeSum = 0;

        for (Double time : times) {
            timeSum += time;
        }
        return (int) Math.round(timeSum/times.size());
    }

    public boolean compareServiceId(TimeQuery timeQuery, TimeQuery timeQueryCompared) {

        for (int i = 0; i < timeQuery.getServiceId().length() && i < timeQueryCompared.getServiceId().length(); i++) {
            if (!(timeQuery.getServiceId().charAt(i) == timeQueryCompared.getServiceId().charAt(i))) {
                return false;
            }
        }

        return true;
    }

    public boolean compareQueryTypeId(TimeQuery timeQuery, TimeQuery timeQueryCompared) {

        if ("*".equals(timeQuery.getQueryTypeId())) {
            return true;
        }

        for (int i = 0; i < timeQuery.getQueryTypeId().length()
                && i < timeQueryCompared.getQueryTypeId().length(); i++) {
            if (!(timeQuery.getQueryTypeId().charAt(i) == timeQueryCompared.getQueryTypeId().charAt(i))) {
                return false;
            }
        }

        return true;
    }

    public boolean compareResponseType(TimeQuery timeQuery, TimeQuery timeQueryCompared) {

        return timeQuery.getResponseType().equals(timeQueryCompared.getResponseType());
    }

    public boolean compareDates(TimeQuery timeQuery, TimeQuery timeQueryCompared) {

        if (timeQuery.getDateFrom().before(timeQueryCompared.getDateFrom())
                || timeQuery.getDateFrom().equals(timeQueryCompared.getDateFrom())) {
            if (timeQuery.getDateTo() != null && timeQueryCompared.getDateTo() != null) {
                if (timeQuery.getDateTo().after(timeQueryCompared.getDateFrom())
                        || timeQuery.getDateTo().equals(timeQueryCompared.getDateFrom())) {
                    return true;
                } else if (timeQuery.getDateTo().after(timeQueryCompared.getDateTo())
                        || timeQuery.getDateTo().equals(timeQueryCompared.getDateTo())) {
                    return true;
                } else {
                    return false;
                }
            } else if (timeQuery.getDateTo() == null) {
                return true;
            } else if (timeQueryCompared.getDateTo() == null) {
                if (timeQuery.getDateTo().after(timeQueryCompared.getDateFrom())
                        || timeQuery.getDateTo().equals(timeQueryCompared.getDateFrom())) {
                    return true;
                } else {
                    return false;
                }
            }
        }

        return false;
    }

    public TimeQuery createObjectC(String[] data) {

        TimeQuery timeQuery = new TimeQuery();

        timeQuery.setObjectType(data[0]);
        timeQuery.setServiceId(data[1]);
        timeQuery.setQueryTypeId(data[2]);
        timeQuery.setResponseType(data[3]);
        timeQuery.setDateFrom(createDate(data[4]));
        timeQuery.setTime(Double.parseDouble(data[5]));

        return timeQuery;
    }

    public TimeQuery createObjectD(String[] data) {

        TimeQuery timeQuery = new TimeQuery();

        timeQuery.setObjectType(data[0]);
        timeQuery.setServiceId(data[1]);
        timeQuery.setQueryTypeId(data[2]);
        timeQuery.setResponseType(data[3]);

        if (data[4].contains("-")) {
            timeQuery.setDateFrom(createDate(data[4].split("-")[0]));
            timeQuery.setDateTo(createDate(data[4].split("-")[1]));
        } else {
            timeQuery.setDateFrom(createDate(data[4]));
        }

        return timeQuery;
    }

    public Date createDate(String stringDate) {

        if (stringDate == null) {
            return null;
        }

        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        Date date = null;

        try {
            date = df.parse(stringDate);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }
}
