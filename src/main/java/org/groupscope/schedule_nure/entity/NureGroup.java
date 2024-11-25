package org.groupscope.schedule_nure.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@EqualsAndHashCode(of = {"id", "name"})
@ToString(of = {"id", "name"})
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "nure_groups")
public class NureGroup {

    @Id
    private Long id;

    @Column
    private String name;
}
