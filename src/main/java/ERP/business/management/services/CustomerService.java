package ERP.business.management.services;

import ERP.business.management.dto.CustomerDTO;
import ERP.business.management.model.customer.Customer;
import ERP.business.management.model.customer.CustomerType;
import ERP.business.management.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Transactional(readOnly = true)
    public List<CustomerDTO> findAll() {
        return customerRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CustomerDTO> findByCustomerType(CustomerType customerType) {
        return customerRepository.findByCustomerType(customerType).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CustomerDTO create(CustomerDTO customerDTO) {
        Customer customer = convertToEntity(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return convertToDTO(savedCustomer);
    }

    @Transactional
    public Optional<CustomerDTO> update(UUID id, CustomerDTO customerDTO) {
        if (!customerRepository.existsById(id)) {
            return  Optional.empty();
        }

        Customer customer = convertToEntity(customerDTO);
        customer.setId(id);
        Customer updateCustomer = customerRepository.save(customer);
        return Optional.of(convertToDTO(updateCustomer));
    }

    @Transactional
    public boolean delete(UUID id) {
        if (!customerRepository.existsById(id)) {
            return false;
        }
        customerRepository.deleteById(id);
        return true;
    }

    private CustomerDTO convertToDTO(Customer customer) {
        return  CustomerDTO.builder()
                .id(customer.getId())
                .name(customer.getName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .address(customer.getAddress())
                .customerType(customer.getCustomerType())
                .build();
    }

    private Customer convertToEntity(CustomerDTO customerDTO) {
        return Customer.builder()
                .id(customerDTO.getId())
                .name(customerDTO.getName())
                .email(customerDTO.getEmail())
                .phone(customerDTO.getPhone())
                .address(customerDTO.getAddress())
                .customerType(customerDTO.getCustomerType())
                .build();
    }
}
