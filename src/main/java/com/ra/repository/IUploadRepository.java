package com.ra.repository;

import com.ra.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUploadRepository extends JpaRepository<Image, Long> {
}
