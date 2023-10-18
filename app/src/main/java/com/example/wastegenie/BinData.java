package com.example.wastegenie;

import java.util.Date;

public class BinData {

    /**
     * Follow data is based on the dummy data on the groupchat - this has been added onto
     * firebase realtime database!
     */

    String binAddress;
    String binName;
    int computerTelecomPercent;
    int consumerElectronicsPercent;
    String councilAddress;
    String councilName;
    Date date;
    int eWasteWeightKilos;
    int id;
    int lightingDevicesPercent;
    int otherPercent;
    int sum;
    int smallAppliancesPercent;
    String status;
    String time;
    String truckId;

    public String getBinAddress() {
        return binAddress;
    }

    public void setBinAddress(String binAddress) {
        this.binAddress = binAddress;
    }

    public String getBinName() {
        return binName;
    }

    public void setBinName(String binName) {
        this.binName = binName;
    }

    public int getComputerTelecomPercent() {
        return computerTelecomPercent;
    }

    public void setComputerTelecomPercent(int computerTelecomPercent) {
        this.computerTelecomPercent = computerTelecomPercent;
    }

    public int getConsumerElectronicsPercent() {
        return consumerElectronicsPercent;
    }

    public void setConsumerElectronicsPercent(int consumerElectronicsPercent) {
        this.consumerElectronicsPercent = consumerElectronicsPercent;
    }

    public String getCouncilAddress() {
        return councilAddress;
    }

    public void setCouncilAddress(String councilAddress) {
        this.councilAddress = councilAddress;
    }

    public String getCouncilName() {
        return councilName;
    }

    public void setCouncilName(String councilName) {
        this.councilName = councilName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int geteWasteWeightKilos() {
        return eWasteWeightKilos;
    }

    public void seteWasteWeightKilos(int eWasteWeightKilos) {
        this.eWasteWeightKilos = eWasteWeightKilos;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLightingDevicesPercent() {
        return lightingDevicesPercent;
    }

    public void setLightingDevicesPercent(int lightingDevicesPercent) {
        this.lightingDevicesPercent = lightingDevicesPercent;
    }

    public int getOtherPercent() {
        return otherPercent;
    }

    public void setOtherPercent(int otherPercent) {
        this.otherPercent = otherPercent;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public int getSmallAppliancesPercent() {
        return smallAppliancesPercent;
    }

    public void setSmallAppliancesPercent(int smallAppliancesPercent) {
        this.smallAppliancesPercent = smallAppliancesPercent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTruckId() {
        return truckId;
    }

    public void setTruckId(String truckId) {
        this.truckId = truckId;
    }

}
