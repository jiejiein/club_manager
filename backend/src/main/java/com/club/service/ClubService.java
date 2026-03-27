package com.club.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.club.dto.ClubDTO;
import com.club.entity.ClubType;
import com.club.vo.ClubVO;

import java.util.List;

public interface ClubService {

    Page<ClubVO> getClubPage(Integer current, Integer size, String keyword, Long typeId, Integer status);

    ClubVO getClubById(Long id);

    void createClub(ClubDTO dto);

    void updateClub(ClubDTO dto);

    void deleteClub(Long id);

    void auditClub(Long id, Integer status, String rejectReason);

    List<ClubType> getAllClubTypes();

    void createClubType(String name, String description, Integer sort);

    void updateClubType(Long id, String name, String description, Integer sort);

    void deleteClubType(Long id);

    List<ClubVO> getMyClubs();

    List<ClubVO> getActiveClubs();

    void quitClub(Long clubId);

    void transferPresident(Long clubId, Long newPresidentId);
}
