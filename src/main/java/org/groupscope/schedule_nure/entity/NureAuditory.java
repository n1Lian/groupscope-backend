package org.groupscope.schedule_nure.entity;


import jakarta.persistence.*;
import lombok.*;

@Getter
@EqualsAndHashCode(of = {"id", "name", "corps"})
@ToString(of = {"id", "name", "corps"})
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "nure_auditories")
public class NureAuditory {

    @Id
    private Long id;

    @Column
    private String name;

    @Column
    private String corps;
}
