package com.tumo.controller;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tumo.jwt.JwtFilter;
import com.tumo.model.LoginDto;
import com.tumo.model.SignupDto;
import com.tumo.model.TokenDto;
import com.tumo.model.UserDto;
import com.tumo.model.service.UserService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/user")
@CrossOrigin("*")
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping(value = "/signup")
	@ApiOperation(value = "회원가입")
	public ResponseEntity createSignup(@RequestBody SignupDto signupDto) {

		ResponseEntity response = null;
		Map<String, Object> resultMap = new HashMap<>();

		try {
			userService.createUser(signupDto);

			// 회원가입 성공
			resultMap.put("message", "success");
			response = new ResponseEntity<Map<String, Object>>(resultMap, HttpStatus.CREATED);
		} catch (SQLException e) {
			e.printStackTrace();

			// DB 오류로 회원가입 실패
			resultMap.put("message", "fail");
			response = new ResponseEntity<Map<String, Object>>(resultMap, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return response;
	}

	@GetMapping(value = "/email/{email}")
	@ApiOperation(value = "email 중복 체크")
	public ResponseEntity ReadEmail(@PathVariable String email) {
		ResponseEntity response = null;
		Map<String, Object> resultMap = new HashMap<>();

		// 사용 가능 true, 중복 false
		boolean check = userService.checkEmail(email);

		if (check) {
			// 사용 가능한 이메일
			resultMap.put("message", "success");
			response = new ResponseEntity<Map<String, Object>>(resultMap, HttpStatus.OK);
		} else {
			// 중복된 이메일 존재
			resultMap.put("message", "fail");
			response = new ResponseEntity<Map<String, Object>>(resultMap, HttpStatus.OK);
		}

		return response;
	}

	@GetMapping(value = "/nickname/{nickname}")
	@ApiOperation(value = "nickname 중복 체크")
	public ResponseEntity ReadNickname(@PathVariable String nickname) {
		ResponseEntity response = null;
		Map<String, Object> resultMap = new HashMap<>();

		// 사용 가능 true, 중복 false
		boolean check = userService.checkNickname(nickname);

		if (check) {
			// 사용 가능한 nickname
			resultMap.put("message", "success");
			response = new ResponseEntity<Map<String, Object>>(resultMap, HttpStatus.OK);
		} else {
			// 중복된 닉네임 존재
			resultMap.put("message", "fail");
			response = new ResponseEntity<Map<String, Object>>(resultMap, HttpStatus.OK);
		}

		return response;
	}
	
	@PostMapping(value = "/login")
	@ApiOperation(value = "로그인 | jwt는 헤더에 autorization으로 전송")
	public ResponseEntity createLogin(@RequestBody LoginDto loginDto) {
		ResponseEntity response = null;
		Map<String, Object> resultMap = new HashMap<>();
		
		try {
			TokenDto tokenDto = userService.login(loginDto);
			
			String jwt = tokenDto.getJwt();
			UserDto userDto = tokenDto.getUserDto();
			
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
			
			resultMap.put("userDto", userDto);
			resultMap.put("message", "ok");
			
			response = new ResponseEntity<>(resultMap, httpHeaders, HttpStatus.OK);
		} catch (Exception e) {
			resultMap.put("message", "로그인 실패");
			response = new ResponseEntity<>(resultMap, HttpStatus.OK);
		}
		
		
		return response;
		
	}
	
	
	@GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<String> getMyUserInfo() {
        return ResponseEntity.ok("ok");
    }
 
    @GetMapping("/user/{username}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<String> getUserInfo(@PathVariable String username) {
        return ResponseEntity.ok("ok");
    }
	

}
