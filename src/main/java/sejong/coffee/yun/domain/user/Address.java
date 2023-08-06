package sejong.coffee.yun.domain.user;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Embeddable
@Getter
@NoArgsConstructor
public class Address {

    @NotBlank
    private String city;
    @NotBlank
    private String district;
    private String detail;
    @Pattern(regexp = "^\\d{3}-\\d{3}")
    private String zipCode;

    @Builder
    public Address(String city, String district, String detail, String zipCode) {
        this.city = city;
        this.district = district;
        this.detail = detail;
        this.zipCode = zipCode;
    }
}
