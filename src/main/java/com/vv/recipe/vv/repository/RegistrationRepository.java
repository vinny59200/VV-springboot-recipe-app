package com.vv.recipe.vv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.vv.recipe.vv.model.VVRegistration;

import java.util.List;

public interface RegistrationRepository extends JpaRepository<VVRegistration, Integer> {


    @Query(value="SELECT * FROM VVREGISTRATION r WHERE LOWER(r.EMAIL) LIKE LOWER(:email)",nativeQuery = true)
    List<VVRegistration> findByEmail(@Param("email")String email);
}
