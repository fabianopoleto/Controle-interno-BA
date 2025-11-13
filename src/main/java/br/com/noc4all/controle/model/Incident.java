package br.com.noc4all.controle.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Incident {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String baGt;
  private LocalDate dataAbertura;
  private String dddOrigem;
  private String origem;
  private String dddDestino;
  private String destino;
  @Column(length = 2000)
  private String descricaoFalha;
  private String operadora;
  private String baOperadora;
  private String status;
  private LocalDate dataEncerramento;
  private String cliente;
}
