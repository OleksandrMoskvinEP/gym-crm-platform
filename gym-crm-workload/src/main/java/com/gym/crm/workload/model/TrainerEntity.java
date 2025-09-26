package com.gym.crm.workload.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedHashSet;
import java.util.Set;

@Document(collection = "trainers")
@CompoundIndex(name = "username_idx", def = "{'firstName': 1, 'lastName':1}")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"years"})
@ToString(exclude = {"years"})
@Getter
public class TrainerEntity {
    @Id
    private String id;

    @Indexed(unique = true)
    private String firstName;
    private String lastName;
    private String username;
    private Boolean isActive;

    @Builder.Default
    private Set<YearEntity> years = new LinkedHashSet<>();

    public void addYear(YearEntity y) {
        years.add(y);
    }

    public void removeYear(YearEntity y) {
        years.remove(y);
    }
}
