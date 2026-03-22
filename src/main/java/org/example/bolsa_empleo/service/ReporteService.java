package org.example.bolsa_empleo.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.example.bolsa_empleo.entidades.Empresa;
import org.example.bolsa_empleo.entidades.Oferente;
import org.example.bolsa_empleo.repository.EmpresaRepository;
import org.example.bolsa_empleo.repository.OferenteRepository;
import org.example.bolsa_empleo.repository.PostulacionRepository;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;

@Service
public class ReporteService {

    private final EmpresaRepository empresaRepository;
    private final OferenteRepository oferenteRepository;
    private final PostulacionRepository postulacionRepository;

    public ReporteService(EmpresaRepository empresaRepository,
                          OferenteRepository oferenteRepository,
                          PostulacionRepository postulacionRepository) {
        this.empresaRepository = empresaRepository;
        this.oferenteRepository = oferenteRepository;
        this.postulacionRepository = postulacionRepository;
    }

    public byte[] generarReporteAdministrativo() {
        List<Empresa> empresas = empresaRepository.findAll();
        List<Oferente> oferentes = oferenteRepository.findAll();

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document doc = new Document(PageSize.A4);
            PdfWriter.getInstance(doc, baos);
            doc.open();

            doc.add(new Paragraph("Bolsa de Empleo - Reporte Administrativo"));
            doc.add(new Paragraph("Fecha: " + LocalDate.now()));
            doc.add(new Paragraph(" "));
            doc.add(new Paragraph("Total empresas: " + empresas.size()));
            doc.add(new Paragraph("Empresas pendientes: " + empresas.stream().filter(e -> Boolean.FALSE.equals(e.getAprobada())).count()));
            doc.add(new Paragraph("Total oferentes: " + oferentes.size()));
            doc.add(new Paragraph("Oferentes pendientes: " + oferentes.stream().filter(o -> Boolean.FALSE.equals(o.getAprobado())).count()));
            doc.add(new Paragraph("Total postulaciones: " + postulacionRepository.count()));
            doc.add(new Paragraph(" "));

            doc.add(new Paragraph("Empresas registradas:"));
            for (Empresa empresa : empresas) {
                doc.add(new Paragraph("- " + empresa.getNombreEmpresa() + " | aprobada: " + empresa.getAprobada()));
            }

            doc.add(new Paragraph(" "));
            doc.add(new Paragraph("Oferentes registrados:"));
            for (Oferente oferente : oferentes) {
                doc.add(new Paragraph("- " + oferente.getNombreOferente() + " " + oferente.getPrimerApellido() + " | aprobado: " + oferente.getAprobado()));
            }

            doc.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("No se pudo generar el PDF", e);
        }
    }
}
