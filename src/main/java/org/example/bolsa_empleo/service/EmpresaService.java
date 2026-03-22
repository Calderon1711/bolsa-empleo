package org.example.bolsa_empleo.service;

import org.example.bolsa_empleo.entidades.Empresa;
import org.example.bolsa_empleo.repository.EmpresaRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public EmpresaService(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    public void registrarEmpresa(Empresa empresa) {
        empresa.setPasswordEmpresa(passwordEncoder.encode(empresa.getPasswordEmpresa()));
        empresa.setFechaRegistroEmpresa(LocalDate.now());
        empresa.setAprobada(false);
        empresaRepository.save(empresa);
    }
}
