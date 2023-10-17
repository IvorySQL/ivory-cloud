package com.highgo.cloud.auth.response;



import com.highgo.cloud.auth.model.CustomUserDetail;
import com.highgo.cloud.auth.model.vo.UserInfoVO;

/**
 *
 */
public class SuccessResponse extends GenericResponse {
    private static final GlobalResponseCode success = GlobalResponseCode.SUCCESS;


    public SuccessResponse() {
        super(success.getCode(), success.getMessage());
    }

    public SuccessResponse(int code, String message) {
        super(code, message);
    }

    public SuccessResponse(CustomUserDetail principal) {
        super(success.getCode(), success.getMessage());

        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setUserId(principal.getUserId());
        userInfoVO.setUserName(principal.getUsername());
        userInfoVO.setRoles(principal.getRoles());

        withData(userInfoVO);

    }
}
