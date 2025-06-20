package com.chat.repository;



import com.chat.entity.Friend;
import com.chat.entity.FriendId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, FriendId> {

    @Query("SELECT f.friendId FROM Friend f WHERE f.userId = :userId")
    List<Long> findFriendIdsByUserId(@Param("userId") Long userId);

}