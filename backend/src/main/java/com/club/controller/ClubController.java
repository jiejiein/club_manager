package com.club.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.club.annotation.Log;
import com.club.common.Result;
import com.club.dto.ClubDTO;
import com.club.entity.ClubType;
import com.club.service.ClubService;
import com.club.vo.ClubVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/club")
public class ClubController {

    @Autowired
    private ClubService clubService;

    @GetMapping("/page")
    public Result<Page<ClubVO>> getClubPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long typeId,
            @RequestParam(required = false) Integer status) {
        return Result.success(clubService.getClubPage(current, size, keyword, typeId, status));
    }

    @GetMapping("/{id}")
    public Result<ClubVO> getClubById(@PathVariable Long id) {
        return Result.success(clubService.getClubById(id));
    }

    @PostMapping
    @Log(operation = "创建社团")
    public Result<Void> createClub(@RequestBody ClubDTO dto) {
        clubService.createClub(dto);
        return Result.success();
    }

    @PutMapping
    @Log(operation = "更新社团")
    public Result<Void> updateClub(@RequestBody ClubDTO dto) {
        clubService.updateClub(dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Log(operation = "删除社团")
    public Result<Void> deleteClub(@PathVariable Long id) {
        clubService.deleteClub(id);
        return Result.success();
    }

    @PutMapping("/audit/{id}")
    @Log(operation = "审核社团")
    public Result<Void> auditClub(@PathVariable Long id, @RequestParam Integer status, 
                                   @RequestParam(required = false) String rejectReason) {
        clubService.auditClub(id, status, rejectReason);
        return Result.success();
    }

    @GetMapping("/types")
    public Result<List<ClubType>> getAllClubTypes() {
        return Result.success(clubService.getAllClubTypes());
    }

    @PostMapping("/type")
    public Result<Void> createClubType(@RequestParam String name, 
                                        @RequestParam(required = false) String description,
                                        @RequestParam(defaultValue = "0") Integer sort) {
        clubService.createClubType(name, description, sort);
        return Result.success();
    }

    @PutMapping("/type/{id}")
    public Result<Void> updateClubType(@PathVariable Long id, @RequestParam String name,
                                        @RequestParam(required = false) String description,
                                        @RequestParam(defaultValue = "0") Integer sort) {
        clubService.updateClubType(id, name, description, sort);
        return Result.success();
    }

    @DeleteMapping("/type/{id}")
    public Result<Void> deleteClubType(@PathVariable Long id) {
        clubService.deleteClubType(id);
        return Result.success();
    }

    @GetMapping("/my")
    public Result<List<ClubVO>> getMyClubs() {
        return Result.success(clubService.getMyClubs());
    }

    @GetMapping("/active")
    public Result<List<ClubVO>> getActiveClubs() {
        return Result.success(clubService.getActiveClubs());
    }

    @DeleteMapping("/quit/{clubId}")
    public Result<Void> quitClub(@PathVariable Long clubId) {
        clubService.quitClub(clubId);
        return Result.success();
    }

    @PutMapping("/transfer/{clubId}")
    @Log(operation = "转移社长")
    public Result<Void> transferPresident(@PathVariable Long clubId, @RequestParam Long newPresidentId) {
        clubService.transferPresident(clubId, newPresidentId);
        return Result.success();
    }
}
