package com.june.swu.domain.location.entity;

import com.june.swu.domain.location.dto.request.LocationRequestDto;
import com.june.swu.domain.post.entity.Post;
import com.june.swu.global.common.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "location")
public class Location extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id")
    private Long id;

    @Column(nullable = false)
    private Float latitude;

    @Column(nullable = false)
    private Float longitude;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column
    private Boolean isActive;

    @PrePersist
    public void prePersist() {
        this.isActive = this.isActive == null ? true : this.isActive;
    }

    public void deleteLocation() {
        this.isActive = false;
    }

    public void updateLocation(LocationRequestDto locationRequestDto) {
        this.latitude = locationRequestDto.getLatitude();
        this.longitude = locationRequestDto.getLongitude();
    }
}
