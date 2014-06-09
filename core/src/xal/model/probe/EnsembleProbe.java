/*
 * EnsembleProbe.java
 *
 * Created on September 17, 2002, 9:29 PM
 */

package xal.model.probe;

import xal.tools.beam.CovarianceMatrix;
import xal.tools.beam.PhaseVector;
import xal.tools.beam.ens.Ensemble;
import xal.tools.data.DataAdaptor;
import xal.tools.math.r3.R3;
import xal.model.probe.traj.EnsembleProbeState;
import xal.model.probe.traj.EnsembleTrajectory;
import xal.model.probe.traj.ProbeState;
import xal.model.probe.traj.Trajectory;
import xal.model.xml.ParsingException;

/**
 * Represents an ensemble of particles.  This <code>IProbe</code> type maintains an
 * <code>Ensemble</code> object which is a collection of <code>Particle</code>s.  Thus,
 * this probe designed for multi-particle simulation.
 *
 * @author  Christopher Allen
 */
public class EnsembleProbe extends BunchProbe {
    
    /*
     *  Global Attributes
     */
    
    /** no field calculation scheme specified */
    public final static int     FLDCALC_NONE = 0;
    
    /** use a full potential summation of each particle */
    public final static int     FLDCALC_SUMMATION = 1;
    
    /** use grid finite difference scheme */
    public final static int     FLDCALC_GRIDFD = 2;
    
    /** use grid Fourier transform method */
    public final static int     FLDCALC_GRIDFT = 3;

    /*
     *  Attributes
     */
    
    /** field calculation method */
    private int         m_enmFldCalc;
    
    /** the particle ensemble */
    private Ensemble    m_ensPhase;
    
    
    
    
    /*
     *  Abstract Method Implementations
     */
    
    
    
    // ************* Probe Trajectory Support
    
    /**
     * Creates a state snapshot of this probe's state and returns it as a 
     * <code>ProbeState</code> object.
     * 
     * @return a <code>EnsembleProbeState</code> encapsulating the probe's current state
     */
    @Override
    public EnsembleProbeState createProbeState() {
        return new EnsembleProbeState(this);
    }
    
    
    /**
     * Creates a trajectory of the proper type for saving the probe's history.
     * 
     * @return  a new, empty <code>EnsembleTrajectory</code> for saving the probe's history
     */
    @Override
    public Trajectory<EnsembleProbeState> createTrajectory() {
        return new Trajectory<EnsembleProbeState>();
    }
    
    


    // BunchProbe Base Support =================================================
    
    /**
     *  Return the coordinates of the ensemble centroid.
     *
     *  @return     (homogeneous) phase space coordinates of ensemble centroid
     */
    public PhaseVector  phaseMean()   {
        return getEnsemble().phaseMean();
    }
    
    /**
     *  Return the correlation matrix of the distribution
     *
     *  @return     symmetric 7x7 covariance matrix in homogeneous coordinates
     *
     *  @see    xal.tools.beam.PhaseMatrix
     */
    public CovarianceMatrix  getCorrelation()    {
        return getEnsemble().phaseCovariance();
    }
    
    

    
    /*
     *  EnsembleProbe Initialization
     */
    
    /** 
     *  Creates a new (empty) instance of EnsembleProbe 
     */
    public EnsembleProbe() {
        super( );
        
        m_ensPhase = new Ensemble();
    };
    
    /**
     *  Copy Constructor.  Create a new instance of <code>EnsembleProbe</code>
     *  which is a deep copy of the argument
     * 
     *  NOTE: the copy operation can be expansive for large <code>Ensemble</code>s
     * 
     *  @param  probe   object to be copied
     */
    public EnsembleProbe(EnsembleProbe probe)   {
        super(probe);
        
        this.setEnsemble( new Ensemble( probe.getEnsemble() ) );
    };
    
    @Override
    public EnsembleProbe copy() {
        return new EnsembleProbe( this );
    }
    /**
     *  Set the field calculation method
     *
     *  @param  enmFldCalc  field calculation method enumeration
     */
    public void setFieldCalculation(int enmFldCalc)  { m_enmFldCalc = enmFldCalc; };
    
    /**
     *  Set the EnsembleProbe state to the value of the argument
     * 
     *  NOTE: the copy operation can be expansive for large <code>Ensemble</code>s
     * 
     *  @param  ens     <code>Ensemble</code> object to be copied
     */
    public void setEnsemble(Ensemble ens)   { 
        m_ensPhase = new Ensemble(ens); 
    };

    
    
    /*
     *  Data Query
     */
    
    /**
     * Return the field calculation method
     */
    public int getFieldCalculation() { return m_enmFldCalc; }
    
    /**
     *  Return the Ensemble state object
     */
    public Ensemble getEnsemble() { return m_ensPhase; };
    

    /**
     *  Get the electric field at a point in R3 from the ensemble.
     *
     *  @param  ptFld       field point to evaluation ensemble field
     *  
     *  @return             electric field at field point
     *
     */
    public R3   electricField(R3 ptFld) {
        R3      vecE = new R3();
        
        return vecE;
    }
    
    
    
    /*
     *  Trajectory Support
     */
 


    /**
     * Apply the contents of ProbeState to update my current state.  The argument
     * supplying the new state should be of concrete type <code>EnsembleProbeState</code>.
     * 
     * @param state     <code>ProbeState</code> object containing new probe state data
     * 
     * @exception   IllegalArgumentException    wrong <code>ProbeState</code> sub-type for this probe
     */
    @Override
    public void applyState(ProbeState state) {
        if (!(state instanceof EnsembleProbeState))
            throw new IllegalArgumentException("invalid probe state");
        super.applyState(state);
        setFieldCalculation(((EnsembleProbeState)state).getFieldCalculation());
        setEnsemble(((EnsembleProbeState)state).getEnsemble());
    }
    
    @Override
    protected ProbeState readStateFrom(DataAdaptor container) throws ParsingException {
        EnsembleProbeState state = new EnsembleProbeState();
        state.load(container);
        return state;
    }
}
