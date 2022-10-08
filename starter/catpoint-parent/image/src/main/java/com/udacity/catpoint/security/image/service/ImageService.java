package com.udacity.catpoint.security.image.service;

import java.awt.image.BufferedImage;

public interface ImageService {

     boolean imageContainsCat(BufferedImage image, float confidenceThreshhold);
}
