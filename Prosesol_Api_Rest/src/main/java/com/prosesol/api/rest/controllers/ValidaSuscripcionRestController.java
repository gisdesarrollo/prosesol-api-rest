package com.prosesol.api.rest.controllers;

import com.prosesol.api.rest.models.entity.Afiliado;
import com.prosesol.api.rest.models.entity.Cliente;
import com.prosesol.api.rest.services.IAfiliadoService;
import com.prosesol.api.rest.services.IClienteService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/pagos")
public class ValidaSuscripcionRestController {

    protected static final Log LOG = LogFactory.getLog(ValidaSuscripcionRestController.class);

    @Autowired
    private IClienteService clienteService;

    @Autowired
    private IAfiliadoService afiliadoService;

    @RequestMapping(value = "/validaSuscripcion", method = RequestMethod.GET)
    @ResponseBody
    public boolean validaSuscripcionPagos(@RequestParam(value = "idAfiliado") Long idAfiliado, HttpServletRequest request,
                                          HttpServletResponse response) {

        try {
            Afiliado afiliado = afiliadoService.findById(idAfiliado);

            Cliente cliente = clienteService.getClienteByIdAfiliado(afiliado);
            if(cliente != null){
                return true;
            }else{
                return false;
            }

        } catch (Exception e) {
            LOG.error("Error al momento de realizar la consulta", e);
        }

        return false;
    }

}
