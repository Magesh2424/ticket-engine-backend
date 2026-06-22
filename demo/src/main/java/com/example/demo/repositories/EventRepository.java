package com.example.demo.repositories;
import com.example.demo.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface EventRepository extends JpaRepository<Event,Long>{

}
