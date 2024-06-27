package com.Xxy.enums;

public enum AppHttpCodeEnum {
    // 成功
    SUCCESS(200,"操作成功"),
    // 登录
    NEED_LOGIN(401,"需要登录后操作"),
    NO_OPERATOR_AUTH(403,"无权限操作"),
    SYSTEM_ERROR(500,"出现错误"),
    USERNAME_EXIST(501,"用户名已存在"),
     PHONENUMBER_EXIST(502,"手机号已存在"), EMAIL_EXIST(503, "邮箱已存在"),
    REQUIRE_USERNAME(504, "必需填写用户名"),
    LOGIN_ERROR(505,"用户名或密码错误"),
    CONTENT_NOT_NULL(506,"内容不能为空"),
    FILE_TYPE_ERROR(507,"文件类型错误，请上传png文件"),
    USERNAME_NOT_NULL(508,"用户名不能为空"),
    NIKENAME_NOT_NULL(509,"昵称不能为空"),
    PASSWORD_NOT_NULL(510,"密码不能为空"),
    EMAIL_NOT_NULL(511,"邮箱不能为空"),
    NIKENAME_EXIST(512,"昵称已经存在了"),

    TAGNAMEEMPTY(513,"tag名称为空"),

    TAGREMARKEMPTY(513,"tag的备注为空"),
    TAGINSERTFIAL(514,"插入标签失败"),

    TAGDELETEFIAL(515,"删除标签失败"),
    TAGFINDFIAL(516,"找不到标签"),
    UPMENUISME(517,"失败，上级菜单不能选择自己");
    int code;
    String msg;

    AppHttpCodeEnum(int code, String errorMessage){
        this.code = code;
        this.msg = errorMessage;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
