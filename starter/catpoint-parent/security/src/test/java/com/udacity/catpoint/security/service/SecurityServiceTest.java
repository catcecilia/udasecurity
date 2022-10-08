package com.udacity.catpoint.security.service;

import com.udacity.catpoint.security.application.StatusListener;
import com.udacity.catpoint.security.data.AlarmStatus;
import com.udacity.catpoint.security.data.ArmingStatus;
import com.udacity.catpoint.security.data.SecurityRepository;
import com.udacity.catpoint.security.data.Sensor;
import com.udacity.catpoint.security.image.service.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SecurityServiceTest {

    @Mock
    private Sensor sensor;

    @Mock
    private ImageService imageService;

    @Mock
    private SecurityRepository securityRepository;

    @Mock
    private BufferedImage image;

    @Mock
    private StatusListener statusListener;
    private Set<Sensor> getSensors(){
        Set<Sensor> sensors = new HashSet<>();
        sensors.add(sensor);
        return sensors;
    }
    @InjectMocks
    SecurityService securityService;

    @BeforeEach
    public void setupEach() {
        MockitoAnnotations.openMocks(this);
    }

    @BeforeEach
    void init(){
        securityService = new SecurityService(securityRepository, imageService);
    }

    //Test 1     If alarm is armed and a sensor becomes activated, put the system into pending alarm status.
    @Test
    public void when_alarmArmed_and_sensorActivated_then_putSystem_into_pendingAlarmStatus(){
        when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.NO_ALARM);
        securityService.changeSensorActivationStatus(sensor, true);

        verify(securityRepository, times(1)).setAlarmStatus(AlarmStatus.PENDING_ALARM);
    }

    //Test 2     If alarm is armed and a sensor becomes activated and the system is already pending alarm, set the alarm status to alarm.
    @Test
    public void when_alarmArmed_and_sensorActivated_systemAlreadyPending_setAlarmtoAlarm(){
        when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.PENDING_ALARM);
        securityService.changeSensorActivationStatus(sensor, true);

        verify(securityRepository, times(1)).setAlarmStatus(AlarmStatus.ALARM);

    }

    //Test 3     If pending alarm and all sensors are inactive, return to no alarm state.
    @Test
    void when_pendingAlarmStatus_allSensorsInactive_setNoAlarm(){
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.PENDING_ALARM);
        securityService.handleSensorDeactivated();
        verify(securityRepository).setAlarmStatus(AlarmStatus.NO_ALARM);
    }


    //Test 4     If alarm is active, change in sensor state should not affect the alarm state.
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void when_AlarmActive_sensorState_alarmState_noEffect(Boolean status){
        when(sensor.getActive()).thenReturn(status);

        securityService.changeSensorActivationStatus(sensor,status);
        verify(securityRepository,never()).setAlarmStatus(any());
    }
    //Test 5     If a sensor is activated while already active and the system is in pending state, change it to alarm state.
    @Test
    void when_AlarmActive_SensorsActive_setPendingtoAlarm(){
        when(sensor.getActive()).thenReturn(true);
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.PENDING_ALARM);

        securityService.changeSensorActivationStatus(sensor, true);
        verify(securityRepository).setAlarmStatus(AlarmStatus.ALARM);
    }
    //Test 6     If a sensor is deactivated while already inactive, make no changes to the alarm state.
    @Test
    void when_SensorDeactivated_SensorInactive_noAlarmChange(){
        when(sensor.getActive()).thenReturn(false);
        securityService.changeSensorActivationStatus(sensor, false );
        verify(securityRepository,never()).setAlarmStatus(any());
    }
    //Test 7     If the image service identifies an image containing a cat while the system is armed-home, put the system into alarm status.
    @Test
    void processImage_cat_armedHome_toAlarm(){
        when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);
        when(imageService.imageContainsCat(image, 50.0f)).thenReturn(true);

        securityService.processImage(image);
        verify(securityRepository).setAlarmStatus(AlarmStatus.ALARM);
    }
    //Test 8     If the image service identifies an image that does not contain a cat, change the status to no alarm as long as the sensors are not active.
    @Test
    void when_CatImageFalse_SensorNotActive_AlartStateOff(){
        when(imageService.imageContainsCat(image, 50.0f)).thenReturn(false);
        when(sensor.getActive()).thenReturn(false);

        securityService.processImage(image);
        verify(securityRepository).setAlarmStatus(AlarmStatus.NO_ALARM);
    }
//    //Test 9     If the system is disarmed, set the status to no alarm.
//    @Test
//
//    //Test 10    If the system is armed, reset all sensors to inactive.
//    @Test
//
    //Test 11    If the system is armed-home while the camera shows a cat, set the alarm status to alarm.
    //@Test
}

