package org.groupscope.schedule_nure.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@EqualsAndHashCode(of = {"id", "fullName", "shortName"})
@ToString(of = {"id", "fullName", "shortName"})
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "nure_teachers")
public class NureTeacher {

    @Id
    private Long id;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "short_name")
    private String shortName;

}
