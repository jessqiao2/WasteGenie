package com.example.wastegenie.DataModels;

import com.example.wastegenie.R;

import java.util.ArrayList;

public class ResourceData {

    private String name;
    private String source;
    private String url;
    private Integer image;

    public ResourceData(String name, String source, String url, Integer image) {
        this.name = name;
        this.source = source;
        this.url = url;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getImage() {
        return image;
    }

    public void setImage(Integer image) {
        this.image = image;
    }

    public static ArrayList<ResourceData> getResourceData() {
        ArrayList<ResourceData> resources = new ArrayList<>();
        resources.add(new ResourceData("Computer Recycling - Managing E-waste", "Planet Ark", "https://recyclingnearyou.com.au/computers/", R.drawable.resource1));
        resources.add(new ResourceData("Recycling Old Phones", "MobileMuster", "https://www.mobilemuster.com.au/", R.drawable.resource2));
        resources.add(new ResourceData("National TV and Computer Recycling Scheme","Tech Collect","https://techcollect.com.au/", R.drawable.resource3));
        resources.add(new ResourceData("Recycle eWaste", "NetWaste", "https://www.netwaste.com.au/ewaste/", R.drawable.resource4));
        resources.add(new ResourceData("E-Waste Collection and Recycling", "EcoActiv", "https://www.ecoactiv.com.au/e-waste-electronic-recycling-disposal/", R.drawable.resource5));
        resources.add(new ResourceData("Ultimate Guide to E-Waste", "Safety Culture", "https://safetyculture.com/topics/waste-management-system/e-waste-management/", R.drawable.resource6));
        return resources;
    }

    /**
     * Method to create an arraylist that adds in the resources and compares the  names. If there
     * is a match, return all the resource's information
     */
    public static ResourceData findResources(String name) {
        ArrayList<ResourceData> resources = ResourceData.getResourceData();

        for(final ResourceData resource: resources) {
            if(resource.getName().toLowerCase().equals(name.toLowerCase())) {
                return resource;
            }
        }

        return null;
    }







}
