/*
 * Copyright (C) 2020 Alban Lafuente
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package hitoridenshi.simulationmanager;

import com.github.kilianB.pcg.fast.PcgRSFast;
import com.github.audreyazura.commonutils.PhysicsTools;
import hitoridenshi.simulationmanager.Particle.CapturedState;
import hitoridenshi.simulationmanager.Particle.CollectionState;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.DataFormatException;
import org.nevec.rjm.BigDecimalMath;

/**
 * To store the data about the solar cell, especially the abscissa (one for the eb file, one for the gen file), and tell if a particle has been or not, as well as the collection side. Also store backAbscissa and frontAbscissa.
 * 
 * @author Alban Lafuente
 */
public class Absorber
{
    private final BigDecimal m_backPosition;
    private final BigDecimal m_frontPosition;
    private final BigDecimal m_notchPosition;
    private final boolean m_zeroAtFront;
    private final SCAPSFunction m_electricField;
    private final String m_absorberFileName;
    private final List<HashMap<String, BigDecimal>> m_traps;
    
    /**
     * Constructor for an absorber with a notch
     * Calculate the notch-created effective electric field and add it to the internal electric field given by SCAPS
     * @param p_SCAPSFile the *.eb file given from SCAPS
     * @param p_bias the bias voltage applied on the absorber
     * @param p_notchPosition the position of the notch in the absorber
     * @param p_conditions the condition of calculation
     * @throws DataFormatException
     * @throws IOException 
     */
    public Absorber(Sample p_sample, boolean p_isZeroAtFront, boolean p_isElectron, BigDecimal p_absorberSize, BigDecimal p_abscissaMultiplier) throws DataFormatException, IOException
    {
        m_absorberFileName = p_sample.getConfigFile().getName();
        HashMap<String, BigDecimal> grading = p_sample.getGrading();
        m_notchPosition = grading.get("notchposition");
        m_traps = p_sample.getTraps();
        
        BigDecimal absorberEnd;
        BigDecimal field0toNotch;
        BigDecimal fieldNotchtoEnd;
        
        m_zeroAtFront = p_isZeroAtFront;
        if(m_zeroAtFront)
        {
            m_frontPosition = CalculationConditions.formatBigDecimal(BigDecimal.ZERO);
            m_backPosition = CalculationConditions.formatBigDecimal(p_absorberSize);
            absorberEnd = m_backPosition;
        }
        else
        {
            m_frontPosition = CalculationConditions.formatBigDecimal(p_absorberSize);
            m_backPosition = CalculationConditions.formatBigDecimal(BigDecimal.ZERO);
            absorberEnd = m_frontPosition;
        }
        
        if (p_isElectron)
        {
            SCAPSFunction internalElectricField = SCAPSFunction.createElectricFieldFromSCAPS(p_sample.getConfigFile(), p_abscissaMultiplier);
            //À refactoriser ?
            if(m_zeroAtFront)
            {
                if (m_notchPosition.compareTo(m_frontPosition) == 0)
                {
                    fieldNotchtoEnd = CalculationConditions.formatBigDecimal((grading.get("notch").subtract(grading.get("back")).divide(m_notchPosition.subtract(m_backPosition), MathContext.DECIMAL128)).divide(PhysicsTools.Q, MathContext.DECIMAL128));
                    field0toNotch = fieldNotchtoEnd;
                }
                else if (m_notchPosition.compareTo(m_backPosition) == 0)
                {
                    field0toNotch = CalculationConditions.formatBigDecimal((grading.get("front").subtract(grading.get("notch")).divide(m_frontPosition.subtract(m_notchPosition), MathContext.DECIMAL128)).divide(PhysicsTools.Q, MathContext.DECIMAL128));
                    fieldNotchtoEnd = field0toNotch;
                }
                else
                {
                    field0toNotch = CalculationConditions.formatBigDecimal((grading.get("front").subtract(grading.get("notch")).divide(m_frontPosition.subtract(m_notchPosition), MathContext.DECIMAL128)).divide(PhysicsTools.Q, MathContext.DECIMAL128));
                    fieldNotchtoEnd = CalculationConditions.formatBigDecimal((grading.get("notch").subtract(grading.get("back")).divide(m_notchPosition.subtract(m_backPosition), MathContext.DECIMAL128)).divide(PhysicsTools.Q, MathContext.DECIMAL128));
                }
            }
            else
            {
                if (m_notchPosition.compareTo(m_frontPosition) == 0)
                {
                    field0toNotch = CalculationConditions.formatBigDecimal((grading.get("back").subtract(grading.get("notch")).divide(m_backPosition.subtract(m_notchPosition), MathContext.DECIMAL128)).divide(PhysicsTools.Q, MathContext.DECIMAL128));
                    fieldNotchtoEnd = field0toNotch;
                }
                else if (m_notchPosition.compareTo(m_backPosition) == 0)
                {   
                    fieldNotchtoEnd = CalculationConditions.formatBigDecimal((grading.get("notch").subtract(grading.get("front")).divide(m_notchPosition.subtract(m_frontPosition), MathContext.DECIMAL128)).divide(PhysicsTools.Q, MathContext.DECIMAL128));
                    field0toNotch = fieldNotchtoEnd;
                }
                else
                {
                    field0toNotch = CalculationConditions.formatBigDecimal((grading.get("back").subtract(grading.get("notch")).divide(m_backPosition.subtract(m_notchPosition), MathContext.DECIMAL128)).divide(PhysicsTools.Q, MathContext.DECIMAL128));
                    fieldNotchtoEnd = CalculationConditions.formatBigDecimal((grading.get("notch").subtract(grading.get("front")).divide(m_notchPosition.subtract(m_frontPosition), MathContext.DECIMAL128)).divide(PhysicsTools.Q, MathContext.DECIMAL128));
                }
            }

            SCAPSFunction notchEffectiveElectricField = new SCAPSFunction(internalElectricField.getAbscissa(), m_notchPosition, field0toNotch, fieldNotchtoEnd, absorberEnd);
            m_electricField = new SCAPSFunction(internalElectricField.add(notchEffectiveElectricField));
        }
        else
        {
            m_electricField = SCAPSFunction.createElectricFieldFromSCAPS(p_sample.getConfigFile(), p_abscissaMultiplier);
        }
    }
    
    public Absorber (Absorber p_absorber)
    {
        m_backPosition = p_absorber.getBackPosition();
        m_notchPosition = p_absorber.getNotchPosition();
        m_frontPosition = p_absorber.getNotchPosition();
        
        m_zeroAtFront = p_absorber.isZeroAtFront();
        
        m_electricField = p_absorber.getElectricField();
        m_absorberFileName = p_absorber.getFileName();
        
        m_traps = p_absorber.getTraps();
    }
    
    public SCAPSFunction getElectricField()
    {
        return new SCAPSFunction(m_electricField);
    }
    
    /**
     * Tell if a particle has been collected or not, and if it has been collected at the back or front 
     * @param p_position the position of the particle
     * @return the collection state of the particle
     */
    public CollectionState giveCollection(BigDecimal p_position)
    {
        CollectionState collection = CollectionState.NOTCOLLECTED;
        
        if(m_zeroAtFront)
        {
            if (p_position.compareTo(m_frontPosition) <= 0)
            {
                collection = CollectionState.FRONTCOLLECTED;
            }
            else if (p_position.compareTo(m_backPosition) >= 0)
            {
                collection = CollectionState.BACKCOLLECTED;
            }
        }
        else
        {
            if (p_position.compareTo(m_frontPosition) >= 0)
            {
                collection = CollectionState.FRONTCOLLECTED;
            }
            else if (p_position.compareTo(m_backPosition) <= 0)
            {
                collection = CollectionState.BACKCOLLECTED;
            }
        }
        
        return collection;
    }
    
    public CapturedState giveCapture(BigDecimal p_particleVelocity, PcgRSFast p_RNG)
    {
        CapturedState captured = CapturedState.FREE;
        
        for (HashMap<String, BigDecimal> trap: m_traps)
        {
            /**
             * Using Phi(t) = Phi_0*exp(-t/tau), Phi: particle flux, tau: particle lifetime
             * tau = 1/(Na*sigma*v), Na: trap density, sigma: cross section, v: particle velocity
             * Therefore the probability for a particle to not be captured is
             * exp(-t/tau)
             * and thus the probability for it to be captured is
             * 1-exp(-t/tau)
             */
            BigDecimal captureProba = BigDecimal.ONE.subtract(BigDecimalMath.exp(CalculationConditions.DT.negate().setScale(128).multiply(trap.get("density").setScale(128)).multiply(trap.get("crosssection").setScale(128)).multiply(p_particleVelocity.setScale(128))));
            
            if (new BigDecimal(p_RNG.nextDouble()).compareTo(captureProba) < 0)
            {
                captured = CapturedState.CAPTURED;
            }
        }
        
        return captured;
    }
    
    public BigDecimal getNotchPosition()
    {
        return m_notchPosition;
    }
    
    public String getFileName()
    {
        return m_absorberFileName;
    }
    
    public BigDecimal getFrontPosition()
    {
        return m_frontPosition;
    }
    
    public BigDecimal getBackPosition()
    {
        return m_backPosition;
    }
    
    public boolean isZeroAtFront()
    {
        return m_zeroAtFront;
    }
    
    public ArrayList<HashMap<String, BigDecimal>> getTraps()
    {
        return new ArrayList(m_traps);
    }
}
