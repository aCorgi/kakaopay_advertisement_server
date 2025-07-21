package com.task.kakaopayadvertisementserver.config.security

import com.task.kakaopayadvertisementserver.exception.UnauthorizedException
import com.task.kakaopayadvertisementserver.service.MemberService
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class BasicAuthenticationProvider(
    private val passwordEncoder: PasswordEncoder,
    private val memberService: MemberService,
) : AuthenticationProvider {
    override fun authenticate(authentication: Authentication): Authentication {
        val email = authentication.name
        val password = authentication.credentials.toString()

        memberService.findByEmailOrNull(email)
            ?.let { member ->
                if (passwordEncoder.matches(password, member.password)) {
                    val userDetails =
                        KakaopayMember(
                            id = member.id,
                            email = member.email,
                            roles = member.authorities,
                        )

                    return UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.roles,
                    )
                }
            }

        throw UnauthorizedException("회원 정보가 없습니다")
    }

    override fun supports(authentication: Class<*>): Boolean {
        return authentication == UsernamePasswordAuthenticationToken::class.java
    }
}

data class KakaopayMember(
    val id: Int,
    val email: String,
    val roles: Set<KakaopayAuthority>,
) : UserDetails {
    override fun getAuthorities() = roles

    override fun getPassword() = null

    override fun getUsername() = email

    override fun isAccountNonExpired() = true

    override fun isAccountNonLocked() = true

    override fun isCredentialsNonExpired() = true

    override fun isEnabled() = true
}

enum class KakaopayAuthority : GrantedAuthority {
    ADMIN,
    USER,
    ;

    override fun getAuthority(): String {
        return name
    }
}
