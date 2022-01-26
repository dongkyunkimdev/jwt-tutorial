package kdk.jwttutorial.repository;

import java.util.Optional;
import kdk.jwttutorial.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

	// Eager 조회로 authorities 정보를 함께 가져옴
	@EntityGraph(attributePaths = "authorities")
	Optional<User> findOneWithAuthoritiesByEmail(String username);
}
