package com.prosesol.api.rest.services;

import com.prosesol.api.rest.models.dao.IWebhookDao;
import com.prosesol.api.rest.models.entity.Webhook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Luis Enrique Morales Soriano
 */

@Service
public class IWebhookServiceImpl implements IWebhookService {

    @Autowired
    private IWebhookDao iWebhookDao;

    @Override
    public void save(Webhook webhook) {
        iWebhookDao.save(webhook);
    }
}
