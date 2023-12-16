package entities;

import entities.util.JsfUtil;
import entities.util.JsfUtil.PersistAction;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@ManagedBean(name = "machineController")
@SessionScoped
public class MachineController implements Serializable {

    @EJB
    private entities.MachineFacade ejbFacade;
    private List<Machine> items = null;
    private Machine selected;
    private Date selectedDate;
    private Employe employe;

    public MachineController() {
        employe  = new Employe();
    }

    public Machine getSelected() {
        return selected;
    }

    public void setSelected(Machine selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private MachineFacade getFacade() {
        return ejbFacade;
    }

    public Machine prepareCreate() {
        selected = new Machine();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("MachineCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("MachineUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("MachineDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null;
            items = null;
        }
    }

    public List<Machine> getItems() {
        if (items == null) {
            if (selectedDate != null) {
                items = getFacade().findByDate(selectedDate);
                System.out.println("Filtered by date. Number of items: " + items.size());
            } else {
                items = getFacade().findAll();
            }
        }
        return items;
    }
    
    
    public Date getSelectedDate() {
        System.out.println("Filtered by date. Number of items: " + items.size());
        return selectedDate;
    }

    public void setSelectedDate(Date selectedDate) {
        items = null;
        this.selectedDate = selectedDate;
        items = getItems();
        System.out.println("Filtered by date. Number of items: " + items.size());
    }

    public List<Machine> getMachinesForEmployee() {
    if (employe != null) {
        return ejbFacade.findByEmployee(employe);
    } else {
        return Collections.emptyList();
    }
}


    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public MachineFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(MachineFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

    public Employe getEmploye() {
        return employe;
    }

    public void setEmploye(Employe employe) {
        this.employe = employe;
    }
    
    public List<Machine> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Machine> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Machine.class)
    public static class MachineControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MachineController controller = (MachineController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "machineController");
            return controller.getFacade().find(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Machine) {
                Machine o = (Machine) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Machine.class.getName()});
                return null;
            }
        }

    }

}
