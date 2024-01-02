package com.alpha.omega.user.repository;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
@RedisHash("user")
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@Builder
public class UserEntity implements Serializable {

    @Id
    private String id;
    @NotBlank
    @Indexed
    private String firstName;
    @NotBlank
    @Indexed
    private String lastName;
    @NotBlank
    private String companyName;
    @NotBlank
    @Size(max = 50)
    @Email
    @Indexed
    private String email;
    @NotBlank
    @Size(max = 120)
    private String password;
    private String externalId;
    private String country;
    private String mailCode;
    private Long created;
    private String contextId;
}
