package com.vv.recipe.vv.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.vv.recipe.vv.exception.BadRequestException;
import com.vv.recipe.vv.model.VVRecipe;
import com.vv.recipe.vv.model.VVRegistration;
import com.vv.recipe.vv.repository.RecipeRepository;
import com.vv.recipe.vv.repository.RegistrationRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

@Service
@Slf4j
public class RecipeService implements UserDetailsService {
    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    public VVRecipe save(VVRecipe recipe) {
        if (recipe.getDate() == null) {
            recipe.setDate(LocalDateTime.now());
        }
        log.info("<## VV ##> saving recipe: " + recipe);
        if (recipe.getId() == null) {
            String owner=SecurityContextHolder.getContext().getAuthentication().getName();
            return recipeRepository.save(recipe.toBuilder().owner(owner).id( generateNewIdCuzHellAutoIncrementFoulsUp() ).build() );
        }
        String owner=SecurityContextHolder.getContext().getAuthentication().getName();
        return recipeRepository.save(recipe.toBuilder().owner(owner).build());
    }

    public VVRegistration saveRegistration(VVRegistration vvRegistration) {
        log.info("<## VV ##> saving vvRegistration: " + vvRegistration);
        return registrationRepository.save(vvRegistration.toBuilder().role("ROLE_USER").id( generateNewIdCuzHellAutoIncrementFoulsUpRegistration() ).build() );
    }

    public void delete(Integer id) {
        log.info("<## VV ##> deleting recipe: " + id);
        recipeRepository.deleteById(id);
    }

    public VVRecipe find(Integer id) {
        VVRecipe result = recipeRepository.findById(id).orElseGet(() -> null);
        log.info("<## VV ##> fetching recipe" + id + ": " + result);
        log.info("VV16 owner [service.find]: " + (result==null?"null":result.getOwner()));
        return result;
    }

    public List<VVRecipe> findAll() {
        return recipeRepository.findAll();
    }


    private int generateNewIdCuzHellAutoIncrementFoulsUp() {
        int newId = 0;
        List<VVRecipe> vvRecipes = new ArrayList<>();
        recipeRepository.findAll().forEach(vvRecipes::add);
        for (VVRecipe vvRecipe : vvRecipes) {
            if (vvRecipe.getId() > newId) {
                newId = vvRecipe.getId();
            }
        }
        newId++;
        return newId;
    }

    private int generateNewIdCuzHellAutoIncrementFoulsUpRegistration() {
        int newId = 0;
        List<VVRegistration> vvRegistrations = new ArrayList<>();
        registrationRepository.findAll().forEach(vvRegistrations::add);
        for (VVRegistration vvRegistration : vvRegistrations) {
            if (vvRegistration.getId() > newId) {
                newId = vvRegistration.getId();
            }
        }
        newId++;
        return newId;
    }

    public List<VVRecipe> search(String name, String category) {
        if (name == null && category == null) {
            throw new BadRequestException();
        } else if (name != null && name.isBlank() && category != null && category.isBlank()) {
            throw new BadRequestException();
        } else if (name != null && !name.isBlank() && category != null && !category.isBlank()) {
            throw new BadRequestException();
        } else if (name != null && !name.isBlank()) {
            log.info("<## VV ##> search by name: " + name);
            return recipeRepository.findByName(name);
        } else /*if(category != null && !category.isBlank())*/ {
            log.info("<## VV ##> search by category: " + category);
            return recipeRepository.findByCategory(category);
        }
    }

    public boolean alreadyExists(String email) {
        log.info("<## VV ##> fetching registration by email: " + email);
        List<VVRegistration> result=registrationRepository.findByEmail(email);
        if (result.isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<VVRegistration> result=registrationRepository.findByEmail(username);
        if (result.isEmpty()) {
            log.info("VV9: User not found->" + username);
           throw new UsernameNotFoundException("User not found");
        }
        UserDetails userDetails = new UserDetailsImpl(result.get(0));
        log.info("VV9: User found->" + userDetails);
        return userDetails;
    }


    class UserDetailsImpl implements UserDetails {
        private final String username;
        private final String password;
        private final List<GrantedAuthority> rolesAndAuthorities;

        public UserDetailsImpl(VVRegistration user) {
            username = user.getEmail().toLowerCase(Locale.ROOT);
            password = user.getPassword();
            rolesAndAuthorities = List.of(new SimpleGrantedAuthority(user.getRole()));
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return rolesAndAuthorities;
        }

        @Override
        public String getPassword() {
            return password;
        }

        @Override
        public String getUsername() {
            return username;
        }

        // 4 remaining methods that just return true
        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }


    }
}
