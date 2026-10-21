package com.resqnet.service;

import com.resqnet.dao.HospitalDao;
import com.resqnet.model.Hospital;
import com.resqnet.model.HospitalStaff;

import java.util.List;

/**
 * Service managing hospital interactions and authorized hospital staff access.
 */
public class HospitalService {

    private final HospitalDao hospitalDao = new HospitalDao();
    private final EmergencyService emergencyService = new EmergencyService();

    public List<Hospital> listHospitals() {
        return hospitalDao.getAllHospitals();
    }

    public Hospital getHospitalById(int hospitalId) {
        return hospitalDao.getHospitalById(hospitalId);
    }

    public HospitalStaff authenticateStaff(String identifier, String password) {
        return hospitalDao.authenticateStaff(identifier, password);
    }

    public boolean updateReportStatus(int reportId, String status, int hospitalId) {
        return emergencyService.updateStatus(reportId, status, hospitalId);
    }
}
