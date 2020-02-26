package com.prosesol.api.rest.services;

import com.prosesol.api.rest.models.dao.IPlanDao;
import com.prosesol.api.rest.models.entity.Plan;
import com.prosesol.api.rest.models.entity.Servicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Luis Enrique Morales Soriano
 */
@Service
public class PlanServiceImpl implements  IPlanService{

    @Autowired
    private IPlanDao planDao;

    @Override
    @Transactional
    public Plan getPlanByIdServicio(Servicio servicio) {
        return planDao.getPlanByIdServicio(servicio);
    }
}
