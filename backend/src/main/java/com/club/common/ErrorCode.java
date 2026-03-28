package com.club.common;

/**
 * 错误码枚举
 * 统一定义系统错误信息
 */
public enum ErrorCode {

    // 系统错误
    SUCCESS(200, "操作成功"),
    ERROR(500, "系统错误"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),

    // 业务错误 - 申请相关
    CLUB_NOT_ACTIVE(1001, "社团未正常运营，无法申请加入"),
    APPLY_PENDING_EXISTS(1002, "已有待审核的申请"),
    ALREADY_MEMBER(1003, "已是该社团成员"),
    APPLY_NOT_FOUND(1004, "申请不存在"),
    APPLY_ALREADY_PROCESSED(1005, "该申请已处理"),
    NO_PERMISSION(1006, "无权操作"),
    NOT_YOUR_CLUB(1007, "只能操作自己社团的申请"),

    // 业务错误 - 活动相关
    ACTIVITY_NOT_FOUND(1101, "活动不存在"),
    ACTIVITY_NOT_PUBLISHED(1102, "活动未开放报名"),
    ACTIVITY_FULL(1103, "报名人数已满"),
    ACTIVITY_SIGN_UP_ENDED(1104, "报名已截止"),
    ACTIVITY_NOT_MEMBER(1105, "只有社团成员才能报名该活动"),
    ACTIVITY_ALREADY_SIGN_UP(1106, "已报名该活动"),
    ACTIVITY_TIME_INVALID(1107, "结束时间不能早于开始时间"),
    ACTIVITY_SIGN_UP_TIME_INVALID(1108, "报名截止时间不能早于报名开始时间"),
    NO_PERMISSION_AUDIT_ACTIVITY(1109, "只有管理员可以审核活动"),
    NO_PERMISSION_CREATE_ACTIVITY(1110, "无权限创建活动"),

    // 业务错误 - 社团相关
    CLUB_NOT_FOUND(1201, "社团不存在"),
    CLUB_ALREADY_EXISTS(1202, "社团名称已存在"),
    ALREADY_PRESIDENT(1203, "您已经是某个社团的社长"),
    NOT_YOUR_CLUB_MANAGE(1204, "只能修改自己管理的社团"),

    // 业务错误 - 用户相关
    USER_NOT_FOUND(1301, "用户不存在"),
    USERNAME_EXISTS(1302, "用户名已存在"),
    OLD_PASSWORD_ERROR(1303, "原密码错误"),

    // 业务错误 - 缴费相关
    PAYMENT_NOT_FOUND(1401, "缴费记录不存在"),
    PAYMENT_ALREADY_PAID(1402, "已缴费，无需重复支付");

    private final Integer code;
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
