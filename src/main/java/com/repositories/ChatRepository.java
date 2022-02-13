package com.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.entity.Chat;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Integer>{

	@Query(value = "select * from chat",nativeQuery = true)
	List<Chat> getAll();
}
