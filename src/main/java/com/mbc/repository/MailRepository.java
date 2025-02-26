package com.mbc.repository;

import com.mbc.entity.MailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MailRepository extends JpaRepository<MailEntity, Long> {

}
