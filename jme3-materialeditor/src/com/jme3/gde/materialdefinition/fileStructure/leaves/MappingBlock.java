/*
 *  Copyright (c) 2009-2018 jMonkeyEngine
 *  All rights reserved.
 * 
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are
 *  met:
 * 
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 
 *  * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 *  TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 *  PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jme3.gde.materialdefinition.fileStructure.leaves;

/**
 *
 * @author Nehon
 */
public abstract class MappingBlock extends LeafStatement {

    protected String leftVar;
    protected String rightVar;
    protected String leftVarSwizzle;
    protected String rightVarSwizzle;
    protected String leftNameSpace;
    protected String rightNameSpace;
    protected String condition;

    public MappingBlock(int lineNumber, String line) {
        super(lineNumber, line);
    }

    protected abstract void updateLine();

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        String old = this.condition;
        if (condition.trim().length() == 0 || condition.equals("<null value>")) {
            condition = null;
        }
        this.condition = condition;
        updateLine();
        fire("condition", old, condition);
    }

    public String getLeftVar() {
        return leftVar;
    }

    public void setLeftVar(String leftVar) {
        this.leftVar = leftVar;
        updateLine();
    }

    public String getRightVar() {
        return rightVar;
    }

    public void setRightVar(String rightVar) {
        this.rightVar = rightVar;
        updateLine();
    }

    public String getRightNameSpace() {
        return rightNameSpace;
    }

    public void setRightNameSpace(String rightnameSpace) {
        String old = this.rightNameSpace;
        this.rightNameSpace = rightnameSpace;
        updateLine();
        fire("rightNameSpace", old, rightnameSpace);
    }

    public String getLeftVarSwizzle() {
        return leftVarSwizzle;
    }

    public void setLeftVarSwizzle(String leftVarSwizzle) {
        String old = this.leftVarSwizzle;
        if (leftVarSwizzle.trim().length() == 0) {
            leftVarSwizzle = null;
        }
        this.leftVarSwizzle = leftVarSwizzle;
        updateLine();
        fire("leftVarSwizzle", old, leftVarSwizzle);
    }

    public String getRightVarSwizzle() {
        return rightVarSwizzle;
    }

    public void setRightVarSwizzle(String rightVarSwizzle) {
        String old = this.rightVarSwizzle;
        this.rightVarSwizzle = rightVarSwizzle;
        if (rightVarSwizzle.trim().length() == 0) {
            rightVarSwizzle = null;
        }
        this.rightVarSwizzle = rightVarSwizzle;
        updateLine();
        fire("rightVarSwizzle", old, rightVarSwizzle);
    }

    public String getLeftNameSpace() {
        return leftNameSpace;
    }

    public void setLeftNameSpace(String leftNameSpace) {
        String old = this.leftNameSpace;
        this.leftNameSpace = leftNameSpace;
        updateLine();
        fire("leftNameSpace", old, leftNameSpace);
    }
    
}
