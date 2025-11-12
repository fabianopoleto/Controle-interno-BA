package br.com.noc4all.controle.spec;

import br.com.noc4all.controle.model.Incident;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class IncidentSpecification {

  public static Specification<Incident> hasStatus(String status) {
    return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
  }

  public static Specification<Incident> hasCliente(String cliente) {
    return (root, query, cb) -> cliente == null ? null : cb.like(cb.lower(root.get("cliente")), "%" + cliente.toLowerCase() + "%");
  }

  public static Specification<Incident> hasOperadora(String operadora) {
    return (root, query, cb) -> operadora == null ? null : cb.like(cb.lower(root.get("operadora")), "%" + operadora.toLowerCase() + "%");
  }

  public static Specification<Incident> hasBaGt(String baGt) {
    return (root, query, cb) -> baGt == null ? null : cb.like(cb.lower(root.get("baGt")), "%" + baGt.toLowerCase() + "%");
  }

  public static Specification<Incident> dataAberturaBetween(LocalDate start, LocalDate end) {
    return (root, query, cb) -> {
      if (start == null && end == null) return null;
      if (start != null && end != null) return cb.between(root.get("dataAbertura"), start, end);
      if (start != null) return cb.greaterThanOrEqualTo(root.get("dataAbertura"), start);
      return cb.lessThanOrEqualTo(root.get("dataAbertura"), end);
    };
  }

  public static Specification<Incident> dataEncerramentoBetween(LocalDate start, LocalDate end) {
    return (root, query, cb) -> {
      if (start == null && end == null) return null;
      if (start != null && end != null) return cb.between(root.get("dataEncerramento"), start, end);
      if (start != null) return cb.greaterThanOrEqualTo(root.get("dataEncerramento"), start);
      return cb.lessThanOrEqualTo(root.get("dataEncerramento"), end);
    };
  }
}
