package com.parasuram.bitespeed_takehome_assignment.repository;

import com.parasuram.bitespeed_takehome_assignment.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Integer> {
    @Query(value = "SELECT * FROM Contacts WHERE (email_id = :email OR phone_number = :phoneNumber) AND link_precedence = 'primary'", nativeQuery = true)
    List<Contact> findPrimaryContactsByEmailOrPhone(@Param("email") String email, @Param("phoneNumber") String phoneNumber);

    @Query(value = "SELECT * FROM Contacts WHERE (email_id = :email OR phone_number = :phoneNumber) AND link_precedence = 'secondary'", nativeQuery = true)
    List<Contact> findSecondaryContactsByEmailOrPhone(@Param("email") String email, @Param("phoneNumber") String phoneNumber);

    @Query(value = "SELECT * FROM Contacts WHERE linked_id = :linkedId", nativeQuery = true)
    List<Contact> findAllByLinkedId(@Param("linkedId") Integer linkedId);
}
