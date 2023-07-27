package app.vinhomes.service;

import app.vinhomes.entity.Account;
import app.vinhomes.entity.Order;
import app.vinhomes.entity.type_enum.CancelRequestStatus;
import app.vinhomes.entity.type_enum.OrderStatus;
import app.vinhomes.entity.worker.CancelRequest;
import app.vinhomes.repository.OrderRepository;
import app.vinhomes.repository.worker.CancelRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CancelRequestService {
    @Autowired
    private CancelRequestRepository cancelRequestRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private TransactionService transactionService;
    public List<CancelRequest> getAllRequest(){
        return cancelRequestRepository.findAll();
    }
    public List<CancelRequest> getAllRequestByWorkerId(long id){
        List<CancelRequest> getList = new ArrayList<>();
        cancelRequestRepository.findAll().stream().forEach(cancelRequest -> {
            if(cancelRequest.getWorker().getAccountId() == id){
                getList.add(cancelRequest);
            };
        });
        return getList;
    }
    public List<CancelRequest> getAllRequest_Status(CancelRequestStatus status){
        List<CancelRequest> getList = new ArrayList<>();
        cancelRequestRepository.findAll().stream().forEach(cancelRequest -> {
            if(cancelRequest.getStatus().equals(status)){
                getList.add(cancelRequest);
            }
        });
        return getList;
    }
    public CancelRequest getCancelRequest(long cancelRequestId){
        Optional<CancelRequest> getCancelRequets = cancelRequestRepository.findById(cancelRequestId);
        if(getCancelRequets.isPresent()){
            return getCancelRequets.get();
        }else{
            return null;
        }
    }
    public boolean checkIfCancelable(Order order, CancelRequest cancelRequest){
        OrderStatus getOrderStatus = order.getStatus();
        CancelRequestStatus getCancelRequestStatus = cancelRequest.getStatus();
        if(getOrderStatus.equals(OrderStatus.PENDING) && getCancelRequestStatus.equals(CancelRequestStatus.PENDING) ){
            return true;
        }
        return false;
    }

    public void updateCancelRequest(CancelRequest cancelRequest, boolean acceptOrNot,boolean isWorker){
        Order getOrder = cancelRequest.getOrder();
        if(isWorker){
            if(acceptOrNot){
                cancelRequest.setStatus(CancelRequestStatus.CANCEL);
            }
        }else{
            if(acceptOrNot){
                cancelRequest.setStatus(CancelRequestStatus.ACCEPT);
            }else{
                cancelRequest.setStatus(CancelRequestStatus.REJECT);
            }
        }
        cancelRequestRepository.save(cancelRequest);
    }
    public void createCancelRequest(CancelRequest cancelRequest){
        cancelRequestRepository.save(cancelRequest);
    }
    public boolean checkIfValidForCreate(Account account , Order order){
        boolean checkIfValid = false;
        LocalDate date = order.getSchedule().getWorkDay().minusDays(1);
        LocalDate dateNow = LocalDate.now();
        if (dateNow.isAfter(date) || dateNow.isEqual(date)) {
            Optional<List<CancelRequest>> getList = cancelRequestRepository.findByWorkerAndOrder(account,order);
            if(getList.isPresent() || getList.isEmpty() == false){
                for (CancelRequest request : getList.get()){
                    if(request.getStatus().equals(CancelRequestStatus.CANCEL)){
                        checkIfValid = true;
                    }else{
                        checkIfValid =  false;
                        break;
                    }
                }
                return checkIfValid;
            }else{
                // duoc quyen tao moi
                return true;
            }
        }
        else {
            return false;
        }
    }
}
