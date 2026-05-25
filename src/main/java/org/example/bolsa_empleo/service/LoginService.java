package org.example.bolsa_empleo.service;

import org.example.bolsa_empleo.entidades.Administrador;
import org.example.bolsa_empleo.entidades.Empresa;
import org.example.bolsa_empleo.entidades.Oferente;
import org.example.bolsa_empleo.repository.AdministradorRepository;
import org.example.bolsa_empleo.repository.EmpresaRepository;
import org.example.bolsa_empleo.repository.OferenteRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {

    private final AdministradorRepository administradorRepository;
    private final EmpresaRepository empresaRepository;
    private final OferenteRepository oferenteRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginService(AdministradorRepository administradorRepository,
                        EmpresaRepository empresaRepository,
                        OferenteRepository oferenteRepository,
                        PasswordEncoder passwordEncoder) {
        this.administradorRepository = administradorRepository;
        this.empresaRepository = empresaRepository;
        this.oferenteRepository = oferenteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Object validarLogin(String usuario, String passwordPlano) {

        if (usuario == null || usuario.isBlank()
                || passwordPlano == null || passwordPlano.isBlank()) {
            return null;
        }

        String usuarioLimpio = usuario.trim();

        Optional<Administrador> adminOpt = administradorRepository.findById(usuarioLimpio)
                .or(() -> administradorRepository.findByCorreoAdministrador(usuarioLimpio));

        if (adminOpt.isPresent()) {
            Administrador admin = adminOpt.get();

            if (passwordCoincide(passwordPlano, admin.getPasswordAdministrador())) {
                return admin;
            }
        }

        Optional<Empresa> empresaOpt = empresaRepository.findByCorreoEmpresa(usuarioLimpio);

        if (empresaOpt.isPresent()) {
            Empresa empresa = empresaOpt.get();

            if (Boolean.TRUE.equals(empresa.getAprobada())
                    && passwordCoincide(passwordPlano, empresa.getPasswordEmpresa())) {
                return empresa;
            }
        }

        Optional<Oferente> oferenteOpt = oferenteRepository.findByCorreoOferente(usuarioLimpio);

        if (oferenteOpt.isPresent()) {
            Oferente oferente = oferenteOpt.get();

            if (Boolean.TRUE.equals(oferente.getAprobado())
                    && passwordCoincide(passwordPlano, oferente.getPasswordOferente())) {
                return oferente;
            }
        }

        return null;
    }

    private boolean passwordCoincide(String passwordPlano, String passwordEncriptado) {
        if (passwordPlano == null || passwordPlano.isBlank()) {
            return false;
        }

        if (passwordEncriptado == null || passwordEncriptado.isBlank()) {
            return false;
        }

        try {
            return passwordEncoder.matches(passwordPlano, passwordEncriptado);
        } catch (Exception e) {
            return false;
        }
    }
}