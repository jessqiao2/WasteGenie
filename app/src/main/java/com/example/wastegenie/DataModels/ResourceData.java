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
        resources.add(new ResourceData("Why Recycle E-waste?", "EcoCycle", "https://ecocycle.com.au/why-do-we-recycle-e-waste/#:~:text=It%20prevents%20toxic%20hazards,aren't%20disposed%20of%20responsibly.", R.drawable.resource2));
        resources.add(new ResourceData("National TV and Computer Recycling Scheme","Tech Collect","https://techcollect.com.au/", R.drawable.resource3));
        resources.add(new ResourceData("Benefits of Recycling E-Waste", "Tech Recyclers", "https://www.atechrecyclers.com.au/top-5-benefits-of-recycling-e-waste/", R.drawable.resource4));
        resources.add(new ResourceData("E-Waste Fact Sheet", "World Health Organisation", "https://www.who.int/news-room/fact-sheets/detail/electronic-waste-(e-waste)", R.drawable.resource5));
        resources.add(new ResourceData("Sydney E-Waste Services", "E Wastec", "https://ewastec.com.au/sydney/", R.drawable.resource6));
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
