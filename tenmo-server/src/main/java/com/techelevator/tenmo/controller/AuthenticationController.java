package com.techelevator.tenmo.controller;

import javax.validation.Valid;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.security.jwt.TokenProvider;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller to authenticate users.
 */
@RestController
public class AuthenticationController {

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private UserDao userDao;
    private AccountDao accountDao;
    private TransferDao transferDao;

    public AuthenticationController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder, UserDao userDao, AccountDao accountDao, TransferDao transferDao) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userDao = userDao;
        this.accountDao = accountDao;
        this.transferDao = transferDao;
    }

    @RequestMapping(value = "/user/pendingapprove/", method = RequestMethod.PUT)
    public void pendingApprove(@RequestBody Transfer transfer) {
        transferDao.updateApprove(transfer.getTransferId());
        accountDao.updateAccountById(transfer.getFromAccountId(), transfer.getToAccountId(), transfer.getAmount());
    }

    @RequestMapping(value = "/transfer/{transferId}", method = RequestMethod.GET)
    public Transfer getTransferByTransferId(@PathVariable long transferId) {
        return transferDao.getTransferByTransferId(transferId);
    }

    @RequestMapping(value = "/account/{transferId}", method = RequestMethod.GET)
    public Account getAccountByTransferId(@PathVariable long transferId) {
        return accountDao.getAccountBytransferId(transferId);
    }

    @RequestMapping(value = "/user/pendingreject/{id}", method = RequestMethod.POST)
    public void pendingReject(@PathVariable long id) {
        transferDao.updateReject(id);
    }

    @RequestMapping(value = "/user/{id}/pendingreview", method = RequestMethod.GET)
    public List<TransferView> getPendingList(@PathVariable Long id) {
        List<TransferView> pendingList = transferDao.getPendingList(id);
        return pendingList;
    }

    @RequestMapping(value = "/user/{userId}/transferview/{transferId}", method = RequestMethod.GET)
    public List<TransferView> getTransferDetails(@PathVariable Long userId, @PathVariable Long transferId) {
        List<TransferView> transferDetails = new ArrayList<>();
        transferDetails = transferDao.getTransferDetails(userId, transferId);
        return transferDetails;
    }

    @RequestMapping(value = "/user/{id}/transferview", method = RequestMethod.GET)
    public List<TransferView> getTransferView(@PathVariable Long id) {
        List<TransferView> fromList = transferDao.getFromTransferByUserId(id);
        List<TransferView> toList = transferDao.getToTransferByUserId(id);
        List<TransferView> resultList = new ArrayList<>();
        resultList.addAll(fromList);
        resultList.addAll(toList);
        return resultList;
    }

    @RequestMapping(value = "/user/sendMoney", method = RequestMethod.PUT)
    public void sendBucks(@RequestBody Transfer transfer) {
         accountDao.updateAccount(transfer.getFromUserId(), transfer.getToUserId(), transfer.getAmount());
         transferDao.create(transfer);
    }

    @RequestMapping(value = "/user/requestMoney", method = RequestMethod.POST)
    public void requestBucks(@RequestBody Transfer transfer) {
        transferDao.create(transfer);
    }

    @RequestMapping(value = "/user/{id}/sendList", method = RequestMethod.GET)
    public List<User> listSendUsers(@PathVariable Long id) {
        return userDao.listSendUsers(id);
    }

    @RequestMapping(value = "/user/{id}/balance", method = RequestMethod.GET)
    public Account viewBalance(@PathVariable Long id) {
         return accountDao.getByAccountId(id);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public LoginResponse login(@Valid @RequestBody LoginDTO loginDto) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.createToken(authentication, false);
        
        User user = userDao.findByUsername(loginDto.getUsername());

        return new LoginResponse(jwt, user);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public void register(@Valid @RequestBody RegisterUserDTO newUser) {
        if (!userDao.create(newUser.getUsername(), newUser.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User registration failed.");
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(path = "/account", method = RequestMethod.GET)
    public BankAccount viewBalance(@PathVariable double balance) throws InvalidEntryException {
        return userDao.get(balance);

    }


    /**
     * Object to return as body in JWT Authentication.
     */
    static class LoginResponse {

        private String token;
        private User user;

        LoginResponse(String token, User user) {
            this.token = token;
            this.user = user;
        }

        public String getToken() {
            return token;
        }

        void setToken(String token) {
            this.token = token;
        }

		public User getUser() {
			return user;
		}

		public void setUser(User user) {
			this.user = user;
		}
    }
}

