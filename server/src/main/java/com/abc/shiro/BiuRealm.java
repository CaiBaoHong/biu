package com.abc.shiro;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.gdiot.device.entity.TAdmin;
import com.gdiot.device.service.AdminService;
import com.gdiot.device.service.PermissionService;
import com.gdiot.device.service.RoleService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;


public class BiuRealm extends AuthorizingRealm {

    @Autowired
    private AdminService adminService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private PermissionService permissionService;

    {
        //设置用于匹配密码的CredentialsMatcher
        HashedCredentialsMatcher hashMatcher = new HashedCredentialsMatcher();
        hashMatcher.setHashAlgorithmName(Sha256Hash.ALGORITHM_NAME);
        hashMatcher.setStoredCredentialsHexEncoded(false);
        hashMatcher.setHashIterations(1024);
        this.setCredentialsMatcher(hashMatcher);
    }


    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        //null usernames are invalid
        if (principals == null) {
            throw new AuthorizationException("PrincipalCollection method argument cannot be null.");
        }

        Long adminId = (Long) getAvailablePrincipal(principals);

        Set<String> roleNames = roleService.getRolesByAdminId(adminId);
        Set<String> permissions = permissionService.getPermissionRulesByAdminId(adminId);

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setRoles(roleNames);
        info.setStringPermissions(permissions);
        return info;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        String username = upToken.getUsername();

        // Null username is invalid
        if (username == null) {
            throw new AccountException("Null usernames are not allowed by this realm.");
        }

        TAdmin adminDB = adminService.selectOne(new EntityWrapper<TAdmin>().eq("aname",username));
        if (adminDB == null) {
            throw new UnknownAccountException("No account found for admin [" + username + "]");
        }

        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(adminDB.getAid(), adminDB.getPwd(), getName());
        if (adminDB.getPwdSalt() != null) {
            info.setCredentialsSalt(ByteSource.Util.bytes(adminDB.getPwdSalt()));
        }

        return info;

    }

}
