package com.udacity.catpoint.security.image.service;

import java.awt.image.BufferedImage;

public interface ImageService {
     public boolean imageContainsCat(BufferedImage image, float confidenceThreshhold);
}
