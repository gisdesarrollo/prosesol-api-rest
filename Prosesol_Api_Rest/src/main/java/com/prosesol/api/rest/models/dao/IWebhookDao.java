package com.prosesol.api.rest.models.dao;

import com.prosesol.api.rest.models.entity.Webhook;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Luis Enrique Morales Soriano
 */
public interface IWebhookDao extends CrudRepository<Webhook, Long> {
}
