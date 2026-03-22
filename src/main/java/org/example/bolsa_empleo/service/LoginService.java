package org.example.bolsa_empleo.service;

import org.example.bolsa_empleo.entidades.Administrador;
import org.example.bolsa_empleo.entidades.Empresa;
import org.example.bolsa_empleo.entidades.Oferente;
import org.example.bolsa_empleo.repository.AdministradorRepository;
import org.example.bolsa_empleo.repository.EmpresaRepository;
import org.example.bolsa_empleo.repository.OferenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class LoginService {

    private final AdministradorRepository administradorRepository;
    private final EmpresaRepository empresaRepository;
    private final OferenteRepository oferenteRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public LoginService(AdministradorRepository administradorRepository,
                        EmpresaRepository empresaRepository,
                        OferenteRepository oferenteRepository) {
        this.administradorRepository = administradorRepository;
        this.empresaRepository = empresaRepository;
        this.oferenteRepository = oferenteRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public Object validarLogin(String correo, String passwordPlano) {

        // ADMIN
        var adminOpt = administradorRepository.findByCorreoAdministrador(correo);
        if (adminOpt.isPresent() &&
                passwordEncoder.matches(passwordPlano, adminOpt.get().getPasswordAdministrador())) {
            return adminOpt.get();
        }

        // EMPRESA
        var empresaOpt = empresaRepository.findByCorreoEmpresa(correo);
        if (empresaOpt.isPresent() &&
                passwordEncoder.matches(passwordPlano, empresaOpt.get().getPasswordEmpresa())) {
            return empresaOpt.get();
        }

        // OFERENTE
        var oferenteOpt = oferenteRepository.findByCorreoOferente(correo);
        if (oferenteOpt.isPresent() &&
                passwordEncoder.matches(passwordPlano, oferenteOpt.get().getPasswordOferente())) {
            return oferenteOpt.get();
        }

        return null; // login inválido
    }
}