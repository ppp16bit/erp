package ERP.business.management.controllers;

import ERP.business.management.dto.CustomerDTO;
import ERP.business.management.model.customer.CustomerType;
import ERP.business.management.services.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class CustomerControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    private ObjectMapper objectMapper;

    private UUID customerId;
    private CustomerDTO customerDTO;
    private List<CustomerDTO> customerList;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();

        customerId = UUID.randomUUID();

        customerDTO = new CustomerDTO();
        customerDTO.setId(customerId);
        customerDTO.setName("customer1");
        customerDTO.setEmail("customer1@test.com");
        customerDTO.setPhone("1234567890");
        customerDTO.setCustomerType(CustomerType.CPF);

        CustomerDTO customer2 = new CustomerDTO();
        customer2.setId(UUID.randomUUID());
        customer2.setName("customer2");
        customer2.setEmail("customer2@test.com");
        customer2.setPhone("9876543210");
        customer2.setCustomerType(CustomerType.CNPJ);

        customerList = Arrays.asList(customerDTO, customer2);
    }

    @Test
    void getAllCustomers_ShouldReturnAllCustomers() throws Exception {
        when(customerService.findAll()).thenReturn(customerList);

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("customer1")))
                .andExpect(jsonPath("$[1].name", is("customer2")));

        verify(customerService, times(1)).findAll();
    }

    @Test
    void getCustomerById_WhenCustomerExists_ShouldReturnCustomer() throws Exception {
        when(customerService.findById(customerId)).thenReturn(Optional.of(customerDTO));

        mockMvc.perform(get("/api/customers/{id}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(customerId.toString())))
                .andExpect(jsonPath("$.name", is("customer1")))
                .andExpect(jsonPath("$.email", is("customer1@test.com")));

        verify(customerService, times(1)).findById(customerId);
    }

    @Test
    void getCustomerById_WhenCustomerDoesNotExist_ShouldReturnNotFound() throws Exception {
        when(customerService.findById(customerId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/customers/{id}", customerId))
                .andExpect(status().isNotFound());

        verify(customerService, times(1)).findById(customerId);
    }

    @Test
    void getCustomerByType_ShouldReturnCustomersOfSpecifiedType() throws Exception {
        List<CustomerDTO> individualCustomers = List.of(customerDTO);
        when(customerService.findByCustomerType(CustomerType.CPF)).thenReturn(individualCustomers);

        mockMvc.perform(get("/api/customers/type/{customerType}", "CPF"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("customer1")))
                .andExpect(jsonPath("$[0].customerType", is("CPF")));

        verify(customerService, times(1)).findByCustomerType(CustomerType.CPF);
    }

    @Test
    void createCustomer_ShouldCreateAndReturnCustomer() throws Exception {
        when(customerService.create(any(CustomerDTO.class))).thenReturn(customerDTO);

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("customer1")))
                .andExpect(jsonPath("$.email", is("customer1@test.com")));

        verify(customerService, times(1)).create(any(CustomerDTO.class));
    }

    @Test
    void updateCustomer_WhenCustomerExists_ShouldUpdateAndReturnCustomer() throws Exception {
        CustomerDTO updatedCustomer = new CustomerDTO();
        updatedCustomer.setId(customerId);
        updatedCustomer.setName("customer1");
        updatedCustomer.setEmail("customer1@test.com");
        updatedCustomer.setPhone("5555555555");
        updatedCustomer.setCustomerType(CustomerType.CPF);

        when(customerService.update(eq(customerId), any(CustomerDTO.class))).thenReturn(Optional.of(updatedCustomer));

        mockMvc.perform(put("/api/customers/{id}", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCustomer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("customer1")))
                .andExpect(jsonPath("$.email", is("customer1@test.com")));

        verify(customerService, times(1)).update(eq(customerId), any(CustomerDTO.class));
    }

    @Test
    void updateCustomer_WhenCustomerDoesNotExist_ShouldReturnNotFound() throws Exception {
        when(customerService.update(eq(customerId), any(CustomerDTO.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/customers/{id}", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isNotFound());

        verify(customerService, times(1)).update(eq(customerId), any(CustomerDTO.class));
    }

    @Test
    void deleteCustomer_WhenCustomerExists_ShouldReturnNoContent() throws Exception {
        when(customerService.delete(customerId)).thenReturn(true);

        mockMvc.perform(delete("/api/customers/{id}", customerId))
                .andExpect(status().isNoContent());

        verify(customerService, times(1)).delete(customerId);
    }

    @Test
    void deleteCustomer_WhenCustomerDoesNotExist_ShouldReturnNotFound() throws Exception {
        when(customerService.delete(customerId)).thenReturn(false);

        mockMvc.perform(delete("/api/customers/{id}", customerId))
                .andExpect(status().isNotFound());

        verify(customerService, times(1)).delete(customerId);
    }
}