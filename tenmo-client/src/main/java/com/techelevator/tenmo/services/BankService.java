package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferView;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;


public class BankService {
    public static String AUTH_TOKEN = "";
    private final String API_BASE_URL;
    public RestTemplate restTemplate = new RestTemplate();

    public BankService(String api_base_url) {
        API_BASE_URL = api_base_url;
    }

    public void pendingApprove(Transfer transfer) {
        try {
            restTemplate.exchange(API_BASE_URL + "/user/pendingapprove/", HttpMethod.PUT, makeTransferEntity(transfer), Transfer.class);
        } catch (RestClientResponseException e) {
            String.format("%s%s",e.getRawStatusCode(),e.getStatusText());
        } catch (ResourceAccessException e) {
            e.getMessage();
        }
    }

    public Transfer getTranserByTransferId(int id) {
        Transfer transfer = new Transfer();
        try {
            transfer = restTemplate.exchange(API_BASE_URL + "/transfer/" + id, HttpMethod.GET, makeAuthEntity(), Transfer.class).getBody();
        } catch (RestClientResponseException e) {
            String.format("%s%s",e.getRawStatusCode(),e.getStatusText());
        } catch (ResourceAccessException e) {
            e.getMessage();
        }
        return transfer;
    }

    public Account getAccountByTransferId(int id) {
        Account account = new Account();
        try {
            account = restTemplate.exchange(API_BASE_URL + "/account/" + id, HttpMethod.GET, makeAuthEntity(), Account.class).getBody();
        } catch (RestClientResponseException e) {
            String.format("%s%s",e.getRawStatusCode(),e.getStatusText());
        } catch (ResourceAccessException e) {
            e.getMessage();
        }
        return account;
    }

    public void pendingReject(int id) {
        try {
            restTemplate.exchange(API_BASE_URL + "/user/pendingreject/" + id, HttpMethod.POST, makeAuthEntity(), Transfer.class);
        } catch (RestClientResponseException e) {
            String.format("%s%s",e.getRawStatusCode(),e.getStatusText());
        } catch (ResourceAccessException e) {
            e.getMessage();
        }
    }

    public TransferView[] getPendingList(int id) {
        TransferView[] pendingList = null;
        try {
            pendingList = restTemplate.exchange(API_BASE_URL + "/user/" + id + "/pendingreview", HttpMethod.GET, makeAuthEntity(), TransferView[].class).getBody();
        } catch (RestClientResponseException e) {
            String.format("%s%s",e.getRawStatusCode(),e.getStatusText());
        } catch (ResourceAccessException e) {
            e.getMessage();
        }
        return pendingList;
    }

    public void requestBucks(Transfer transfer) {
        try {
            restTemplate.exchange(API_BASE_URL + "/user/requestMoney", HttpMethod.POST, makeTransferEntity(transfer), Transfer.class);
        } catch (RestClientResponseException e) {
            String.format("%s%s",e.getRawStatusCode(),e.getStatusText());
        } catch (ResourceAccessException e) {
            e.getMessage();
        }
    }

    public Account getAccount(int id) {
        Account account = new Account();
        try {
            account = restTemplate.exchange(API_BASE_URL + "/user/" + id + "/balance", HttpMethod.GET, makeAuthEntity(), Account.class).getBody();
        } catch (RestClientResponseException e) {
            String.format("%s%s",e.getRawStatusCode(),e.getStatusText());
        } catch (ResourceAccessException e) {
            e.getMessage();
        }
        return account;
    }

    public User[] displaySendList(int id) {
        User[] user = null;
        try {
            user = restTemplate.exchange(API_BASE_URL + "/user/" + id + "/sendList", HttpMethod.GET, makeAuthEntity(), User[].class).getBody();
        } catch (RestClientResponseException e) {
            String.format("%s%s",e.getRawStatusCode(),e.getStatusText());
        } catch (ResourceAccessException e) {
            e.getMessage();
        }
        return user;
    }

    public void sendBucks(Transfer transfer) {
        try {
            restTemplate.exchange(API_BASE_URL + "/user/sendMoney", HttpMethod.PUT, makeTransferEntity(transfer), Transfer.class);
        } catch (RestClientResponseException e) {
            String.format("%s%s",e.getRawStatusCode(),e.getStatusText());
        } catch (ResourceAccessException e) {
            e.getMessage();
        }
    }

    public TransferView[] displayTransfer(int id) {
        TransferView[] transferViews = null;
        try {
            transferViews = restTemplate.exchange(API_BASE_URL + "/user/" + id + "/transferview", HttpMethod.GET, makeAuthEntity(), TransferView[].class).getBody();
        } catch (RestClientResponseException e) {
            String.format("%s%s",e.getRawStatusCode(),e.getStatusText());
        } catch (ResourceAccessException e) {
            e.getMessage();
        }
        return transferViews;
    }

    public TransferView[] displayTransferDetails(int userId, int transferId) {
        TransferView[] transferViews = null;
        try {
            transferViews = restTemplate.exchange(API_BASE_URL + "/user/" + userId + "/transferview/" + transferId, HttpMethod.GET, makeAuthEntity(), TransferView[].class).getBody();
        } catch (RestClientResponseException e) {
            String.format("%s%s",e.getRawStatusCode(),e.getStatusText());
        } catch (ResourceAccessException e) {
            e.getMessage();
        }
        return transferViews;
    }

    private HttpEntity makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(AUTH_TOKEN);
        HttpEntity entity = new HttpEntity<>(headers);
        return entity;
    }

    private HttpEntity<Transfer> makeTransferEntity(Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(AUTH_TOKEN);
        HttpEntity<Transfer> entity = new HttpEntity<>(transfer, headers);
        return entity;
    }


}
