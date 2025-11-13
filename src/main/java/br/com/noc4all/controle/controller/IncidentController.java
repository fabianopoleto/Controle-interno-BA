package br.com.noc4all.controle.controller;

import br.com.noc4all.controle.model.Incident;
import br.com.noc4all.controle.repo.IncidentRepository;
import br.com.noc4all.controle.spec.IncidentSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/incidents")
@CrossOrigin(origins = "*")
public class IncidentController {

  private final IncidentRepository repo;

  public IncidentController(IncidentRepository repo) {
    this.repo = repo;
  }

  @GetMapping
  public List<Incident> all() {
    return repo.findAll();
  }

  @GetMapping("/{id}")
  public Incident get(@PathVariable Long id) {
    return repo.findById(id).orElseThrow(() -> new RuntimeException("Registro não encontrado"));
  }

  @PostMapping
  public Incident create(@RequestBody Incident incident) {
    return repo.save(incident);
  }

  @PutMapping("/{id}")
  public Incident update(@PathVariable Long id, @RequestBody Incident newIncident) {
    return repo.findById(id).map(incident -> {
      incident.setBaGt(newIncident.getBaGt());
      incident.setDataAbertura(newIncident.getDataAbertura());
      incident.setDddOrigem(newIncident.getDddOrigem());
      incident.setOrigem(newIncident.getOrigem());
      incident.setDddDestino(newIncident.getDddDestino());
      incident.setDestino(newIncident.getDestino());
      incident.setDescricaoFalha(newIncident.getDescricaoFalha());
      incident.setOperadora(newIncident.getOperadora());
      incident.setBaOperadora(newIncident.getBaOperadora());
      incident.setStatus(newIncident.getStatus());
      incident.setDataEncerramento(newIncident.getDataEncerramento());
      incident.setCliente(newIncident.getCliente());
      return repo.save(incident);
    }).orElseThrow(() -> new RuntimeException("Registro não encontrado"));
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    repo.deleteById(id);
  }

  @GetMapping("/search")
  public List<Incident> search(
      @RequestParam(required = false) String status,
      @RequestParam(required = false) String cliente,
      @RequestParam(required = false) String operadora,
      @RequestParam(required = false) String baGt,
      @RequestParam(required = false) String dataAberturaInicio,
      @RequestParam(required = false) String dataAberturaFim,
      @RequestParam(required = false) String dataEncerramentoInicio,
      @RequestParam(required = false) String dataEncerramentoFim
  ) {
    LocalDate daInicio = dataAberturaInicio != null ? LocalDate.parse(dataAberturaInicio) : null;
    LocalDate daFim = dataAberturaFim != null ? LocalDate.parse(dataAberturaFim) : null;
    LocalDate deInicio = dataEncerramentoInicio != null ? LocalDate.parse(dataEncerramentoInicio) : null;
    LocalDate deFim = dataEncerramentoFim != null ? LocalDate.parse(dataEncerramentoFim) : null;

    Specification<Incident> spec = Specification.where(IncidentSpecification.hasStatus(status))
        .and(IncidentSpecification.hasCliente(cliente))
        .and(IncidentSpecification.hasOperadora(operadora))
        .and(IncidentSpecification.hasBaGt(baGt))
        .and(IncidentSpecification.dataAberturaBetween(daInicio, daFim))
        .and(IncidentSpecification.dataEncerramentoBetween(deInicio, deFim));

    return repo.findAll(spec);
  }

  @GetMapping("/export/csv")
  public ResponseEntity<byte[]> exportCsv(
      @RequestParam(required = false) String status,
      @RequestParam(required = false) String cliente,
      @RequestParam(required = false) String operadora
  ) {
    Specification<Incident> spec = Specification.where(IncidentSpecification.hasStatus(status))
        .and(IncidentSpecification.hasCliente(cliente))
        .and(IncidentSpecification.hasOperadora(operadora));

    List<Incident> lista = repo.findAll(spec);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintWriter pw = new PrintWriter(baos);
    pw.println("BA GT,Data Abertura,DDD Origem,Origem,DDD Destino,Destino,Descricao,Operadora,BA Operadora,Status,Data Encerramento,Cliente");

    for (Incident i : lista) {
        String linha = String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"",
            safe(i.getBaGt()), safe(i.getDataAbertura()), safe(i.getDddOrigem()), safe(i.getOrigem()),
            safe(i.getDddDestino()), safe(i.getDestino()), safe(i.getDescricaoFalha()),
            safe(i.getOperadora()), safe(i.getBaOperadora()), safe(i.getStatus()),
            safe(i.getDataEncerramento()), safe(i.getCliente())
        );
        pw.println(linha);
    }
    pw.flush();

    byte[] data = baos.toByteArray();
    HttpHeaders headers = new HttpHeaders();
    headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=incidentes.csv");
    headers.setContentType(MediaType.parseMediaType("text/csv; charset=utf-8"));

    return ResponseEntity.ok().headers(headers).body(data);
  }

  private String safe(Object o) {
    return o == null ? "" : o.toString().replace(""", """");
  }

  @GetMapping("/stats/by-status")
  public java.util.Map<String, Long> statsByStatus() {
    List<Incident> all = repo.findAll();
    return all.stream().collect(Collectors.groupingBy(i -> i.getStatus() == null ? "N/A" : i.getStatus(), Collectors.counting()));
  }

  @GetMapping("/stats/by-operadora")
  public java.util.Map<String, Long> statsByOperadora() {
    List<Incident> all = repo.findAll();
    return all.stream().collect(Collectors.groupingBy(i -> i.getOperadora() == null ? "N/A" : i.getOperadora(), Collectors.counting()));
  }

}
