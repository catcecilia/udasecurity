module com.udacity.catpoint.security {
    exports com.udacity.catpoint.security.application;
    exports com.udacity.catpoint.security.service;
    exports com.udacity.catpoint.security.data;

    requires java.desktop;
    requires com.google.common;
    requires com.google.gson;
    requires java.prefs;
    requires com.udacity.catpoint.image;
}